package potatowoong.springchat.domain.chat.repository

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import potatowoong.springchat.domain.chat.dto.LastMessageDto
import potatowoong.springchat.domain.chat.dto.MyChatRoomsDto
import potatowoong.springchat.domain.chat.entity.ChatMessage
import java.time.ZoneId
import java.util.*

class ChatMessageRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
) : ChatMessageRepositoryCustom {

    override fun findLastMessages(chatRoomIds: List<String>): List<ChatMessage> {
        val matchOperation = Aggregation.match(
            Criteria.where("chatRoomId").`in`(chatRoomIds)
        )

        val sortOperation = Aggregation.sort(
            Sort.by(
                Sort.Order.desc("_id")
            )
        )

        val groupOperation = Aggregation.group("chatRoomId")
            .first("chatRoomId").`as`("chatRoomId")
            .first("content").`as`("content")
            .first("memberId").`as`("memberId")
            .first("sendAt").`as`("sendAt")

        val aggregation = Aggregation.newAggregation(matchOperation, sortOperation, groupOperation)

        return mongoTemplate.aggregate(aggregation, "chat_message", ChatMessage::class.java).mappedResults
    }
    
    override fun findUnreadMessageCount(myChatRooms: List<MyChatRoomsDto>): List<LastMessageDto> {
        val matchCriteria = myChatRooms.map {
            Criteria().andOperator(
                Criteria.where("chatRoomId").`is`(it.chatRoomId),
                Criteria.where("sendAt").gt(Date.from(it.lastJoinedAt.atZone(ZoneId.systemDefault()).toInstant()))
            )
        }

        val matchOperation = Aggregation.match(
            Criteria().orOperator(
                matchCriteria,
            )
        )

        val sortOperation = Aggregation.sort(
            Sort.by(
                Sort.Order.desc("_id")
            )
        )

        val groupOperation = Aggregation.group("chatRoomId")
            .first("chatRoomId").`as`("chatRoomId")
            .count().`as`("unreadCount")

        val aggregation = Aggregation.newAggregation(matchOperation, sortOperation, groupOperation)

        return mongoTemplate.aggregate(aggregation, "chat_message", LastMessageDto::class.java).mappedResults
    }
}