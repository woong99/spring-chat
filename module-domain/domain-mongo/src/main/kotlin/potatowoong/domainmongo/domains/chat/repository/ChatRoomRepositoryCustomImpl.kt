package potatowoong.domainmongo.domains.chat.repository

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.MongoExpression
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Size
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.aggregation.VariableOperators
import org.springframework.data.mongodb.core.query.Criteria
import potatowoong.domainmongo.domains.chat.dto.MyChatRoomsDto

class ChatRoomRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
) : ChatRoomRepositoryCustom {

    override fun findMyChatRooms(memberId: Long): List<MyChatRoomsDto> {
        // memberId로 채팅방 멤버 필터링
        val matchOperation = Aggregation.match(
            Criteria.where("memberId").`in`(memberId)
        )

        // 마지막 메시지 조회
        val chatMessageLookupOperation = Aggregation.lookup()
            .from("chat")
            .localField("chatRoomId")
            .foreignField("chatRoomId")
            .pipeline(
                Aggregation.sort(
                    Sort.by(
                        Sort.Order.desc("sendAt")
                    )
                ),
                Aggregation.limit(1),
                Aggregation.project(
                    Fields.from(
                        Fields.field("content", "content"),
                        Fields.field("sendAt", "sendAt"),
                        Fields.field("chatRoomId", "chatRoomId")
                    )
                )
            )
            .`as`("last_message")

        val lastMessageUnwindOperation = Aggregation.unwind("last_message", true)

        // 채팅방 조회
        val chatRoomLookupOperation = Aggregation.lookup()
            .from("chat_room")
            .localField("chatRoomId")
            .foreignField("_id")
            .`as`("chat_rooms")

        val chatRoomUnwindOperation = Aggregation.unwind("chat_rooms", true)

        // 채팅방 인원 조회
        val participantCountLookupOperation = Aggregation.lookup()
            .from("chat_room_member")
            .localField("chatRoomId")
            .foreignField("chatRoomId")
            .`as`("chat_room_members")

        val participantCountAddFieldsOperation = Aggregation.addFields()
            .addFieldWithValue("participantCount", Size.lengthOfArray("chat_room_members"))
            .build()

        // 안읽은 메시지 수 조회
        val unreadMessageCountLookupOperation = Aggregation.lookup()
            .from("chat")
            .localField("chatRoomId")
            .foreignField("chatRoomId")
            .let(VariableOperators.Let.ExpressionVariable.newVariable("last_joined_at").forField("lastJoinedAt"))
            .pipeline(
                Aggregation.sort(
                    Sort.by(
                        Sort.Order.desc("sendAt")
                    )
                ),
                Aggregation.match(
                    Criteria.expr(MongoExpression.create("{\$gt: [\"\$sendAt\", \"\$\$last_joined_at\"]}"))
                ),
                Aggregation.limit(100),
                Aggregation.project(
                    Fields.from(
                        Fields.field("id", "_id"),
                    )
                )
            )
            .`as`("unread_message")

        val unreadMessageCountAddFieldsOperation = Aggregation.addFields()
            .addFieldWithValue("unreadMessageCount", Size.lengthOfArray("unread_message"))
            .build()

        // 정렬
        val sortOperation = Aggregation.sort(
            Sort.by(
                Sort.Order.desc("last_message.sendAt")
            )
        )

        val projectOperation = Aggregation.project(
            Fields.from(
                Fields.field("chatRoomId", "chatRoomId"),
                Fields.field("lastMessage", "last_message.content"),
                Fields.field("lastSendAt", "last_message.sendAt"),
                Fields.field("chatRoomName", "chat_rooms.name"),
                Fields.field("participantCount", "participantCount"),
                Fields.field("unreadMessageCount", "unreadMessageCount")
            )
        )

        val aggregation =
            Aggregation.newAggregation(
                matchOperation,
                chatMessageLookupOperation,
                lastMessageUnwindOperation,
                chatRoomLookupOperation,
                chatRoomUnwindOperation,
                participantCountLookupOperation,
                participantCountAddFieldsOperation,
                unreadMessageCountLookupOperation,
                unreadMessageCountAddFieldsOperation,
                sortOperation,
                projectOperation
            )


        return mongoTemplate.aggregate(
            aggregation,
            "chat_room_member",
            MyChatRoomsDto::class.java
        ).mappedResults
    }
}
