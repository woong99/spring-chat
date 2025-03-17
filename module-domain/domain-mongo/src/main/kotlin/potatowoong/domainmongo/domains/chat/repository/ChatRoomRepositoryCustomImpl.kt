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
        // memberId로 채팅방 필터링
        val chatRoomMemberLookupOperation = Aggregation.lookup()
            .from("chat_room_member")
            .localField("_id")
            .foreignField("chatRoomId")
            .let(VariableOperators.Let.ExpressionVariable.newVariable("chatRoomId").forField("_id"))
            .pipeline(
                Aggregation.match(
                    Criteria.where("memberId").`is`(memberId)
                )
            )
            .`as`("chat_room_member")

        val chatRoomMemberUnwindOperation = Aggregation.unwind("chat_room_member", false)

        // 채팅방 인원 조회
        val participantCountLookupOperation = Aggregation.lookup()
            .from("chat_room_member")
            .localField("_id")
            .foreignField("chatRoomId")
            .`as`("chat_room_members")

        val participantCountAddFieldsOperation = Aggregation.addFields()
            .addFieldWithValue("participantCount", Size.lengthOfArray("chat_room_members"))
            .build()

        // 마지막 메시지 조회
        val lastMessageLookupOperation = Aggregation.lookup()
            .from("chat")
            .localField("_id")
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
                    )
                )
            )
            .`as`("last_message")

        val lastMessageUnwindOperation = Aggregation.unwind("last_message", true)

        // 안읽은 메시지 수 조회
        val unreadMessageCountLookupOperation = Aggregation.lookup()
            .from("chat")
            .localField("_id")
            .foreignField("chatRoomId")
            .let(
                VariableOperators.Let.ExpressionVariable.newVariable("last_joined_at")
                    .forField("chat_room_member.lastJoinedAt")
            )
            .pipeline(
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
                Fields.field("chatRoomId", "_id"),
                Fields.field("lastMessage", "last_message.content"),
                Fields.field("lastSendAt", "last_message.sendAt"),
                Fields.field("chatRoomName", "name"),
                Fields.field("participantCount", "participantCount"),
                Fields.field("unreadMessageCount", "unreadMessageCount")
            )
        )

        val aggregation =
            Aggregation.newAggregation(
                chatRoomMemberLookupOperation,
                chatRoomMemberUnwindOperation,
                participantCountLookupOperation,
                participantCountAddFieldsOperation,
                lastMessageLookupOperation,
                lastMessageUnwindOperation,
                unreadMessageCountLookupOperation,
                unreadMessageCountAddFieldsOperation,
                sortOperation,
                projectOperation
            )


        return mongoTemplate.aggregate(
            aggregation,
            "chat_room",
            MyChatRoomsDto::class.java
        ).mappedResults
    }
}
