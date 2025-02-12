package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_message")
class ChatMessage(

    @Id
    val id: String? = null,

    @Column(nullable = false)
    val content: String,

    val memberId: Long,

    val chatRoomId: String,

    val sendAt: LocalDateTime = LocalDateTime.now()
) {
}