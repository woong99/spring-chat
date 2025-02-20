package potatowoong.springchat.domain.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import potatowoong.springchat.domain.chat.entity.Chat

interface ChatRepository : MongoRepository<Chat, String>, ChatRepositoryCustom {

    fun findTop100ByChatRoomIdOrderBySendAtDesc(chatRoomId: ObjectId): List<Chat>
}
