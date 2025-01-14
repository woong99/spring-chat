package potatowoong.springchat.domain.chat.repository

import org.springframework.data.jpa.repository.JpaRepository
import potatowoong.springchat.domain.chat.entity.Chat

interface ChatRepository : JpaRepository<Chat, String> {

    fun findAllByChatRoomChatRoomIdOrderBySendAtDesc(chatRoomId: String): List<Chat>

    fun findTop100ByChatRoomChatRoomIdOrderBySendAtDesc(chatRoomId: String): List<Chat>
}
