package potatowoong.springchat.domain.chat.repository

import org.springframework.data.mongodb.repository.MongoRepository
import potatowoong.springchat.domain.chat.entity.ChatMessage

interface ChatMessageRepository : MongoRepository<ChatMessage, String>, ChatMessageRepositoryCustom {

    fun findAllByChatRoomIdOrderByIdDesc(chatRoomId: String): List<ChatMessage>
}
