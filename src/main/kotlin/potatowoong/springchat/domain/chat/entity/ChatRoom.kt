package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.global.config.db.entity.BaseEntity

@Entity
class ChatRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    var name: String
) : BaseEntity() {
    companion object {
        fun of(
            request: ChatRoomDto.Request
        ) = ChatRoom(
            name = request.name
        )
    }
}