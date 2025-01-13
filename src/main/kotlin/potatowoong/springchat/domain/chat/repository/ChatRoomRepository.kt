package potatowoong.springchat.domain.chat.repository

import org.springframework.data.jpa.repository.JpaRepository
import potatowoong.springchat.domain.chat.entity.ChatRoom

interface ChatRoomRepository : JpaRepository<ChatRoom, String> {
}