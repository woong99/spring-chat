package potatowoong.domainmongo.domains.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import potatowoong.domainmongo.domains.chat.entity.Chat

interface ChatRepository : MongoRepository<Chat, String>, ChatRepositoryCustom {

    fun findTop100ByChatRoomIdOrderBySendAtDesc(chatRoomId: ObjectId): List<Chat>
}
