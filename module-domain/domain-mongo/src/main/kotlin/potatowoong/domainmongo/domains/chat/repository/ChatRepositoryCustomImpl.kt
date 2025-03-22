package potatowoong.domainmongo.domains.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import potatowoong.domainmongo.domains.chat.entity.Chat

class ChatRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
) : ChatRepositoryCustom {

    override fun findMessagesWithPaging(
        chatRoomId: String,
        page: Int
    ): List<Chat> {
        return mongoTemplate.find<Chat>(
            Query.query(
                Criteria().andOperator(
                    Criteria.where("chatRoomId").`is`(ObjectId(chatRoomId)),
                )
            )
                .with(Sort.by(Sort.Order.desc("_id")))
                .skip(page.toLong() * 50)
                .limit(51),
        )
    }
}