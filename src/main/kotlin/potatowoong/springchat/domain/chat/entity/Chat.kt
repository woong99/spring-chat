package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.*
import potatowoong.springchat.domain.auth.entity.Member
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.global.config.db.entity.BaseEntity

@Entity
class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val chatId: String? = null,

    @Column(nullable = false)
    val content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    val chatRoom: ChatRoom
) : BaseEntity() {
    companion object {
        fun of(
            request: MessageDto.Request,
            member: Member,
            chatRoom: ChatRoom
        ) = Chat(
            content = request.message,
            member = member,
            chatRoom = chatRoom
        )
    }
}