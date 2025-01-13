package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.*
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.global.config.db.entity.BaseEntity

@Entity
class ChatRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val chatRoomId: String? = null,

    @Column(nullable = false)
    var name: String,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom")
    val chats: List<Chat> = mutableListOf()
) : BaseEntity() {
    companion object {
        fun of(
            request: ChatRoomDto.Request
        ) = ChatRoom(
            name = request.name
        )
    }
}
