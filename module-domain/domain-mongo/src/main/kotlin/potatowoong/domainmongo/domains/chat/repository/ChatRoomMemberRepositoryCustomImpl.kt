package potatowoong.domainmongo.domains.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import potatowoong.domainmongo.domains.chat.entity.ChatRoomMember
import java.time.LocalDateTime

class ChatRoomMemberRepositoryCustomImpl(
    private val mongoTemplate: MongoTemplate
) : ChatRoomMemberRepositoryCustom {

    override fun updateLastJoinedAt(id: ObjectId) {
        val query = Query.query(Criteria.where("_id").`is`(id))
        val update = Update().set("lastJoinedAt", LocalDateTime.now())
        mongoTemplate.updateFirst(query, update, ChatRoomMember::class.java)
    }
}