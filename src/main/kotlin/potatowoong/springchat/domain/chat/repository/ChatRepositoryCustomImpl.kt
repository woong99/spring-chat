package potatowoong.springchat.domain.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import potatowoong.springchat.domain.chat.entity.Chat

class ChatRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
) : ChatRepositoryCustom {

    override fun findMessagesWithPaging(chatRoomId: String, page: Long): List<Chat> {
        return mongoTemplate.find(
            Query.query(
                Criteria().andOperator(
                    Criteria.where("chatRoomId").`is`(ObjectId(chatRoomId)),
                )
            )
                .with(Sort.by(Sort.Order.desc("_id")))
                .skip(page * 50)
                .limit(51),
            Chat::class.java
        )
    }
}