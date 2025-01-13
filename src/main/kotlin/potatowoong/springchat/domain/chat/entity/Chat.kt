package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.*
import potatowoong.springchat.domain.auth.entity.Member
import potatowoong.springchat.domain.chat.dto.MessageDto
import java.time.LocalDateTime

@Entity
@Table(
    indexes = [
        Index(name = "idx_chat_room_id", columnList = "chat_room_id"),
        Index(name = "idx_member_id", columnList = "member_id")
    ]
)
class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val chatId: String? = null,

    @Column(nullable = false)
    val content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "member_id",
        nullable = false,
        updatable = false,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "chat_room_id",
        nullable = false,
        updatable = false,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val chatRoom: ChatRoom,

    @Column(nullable = false, updatable = false)
    val sendAt: LocalDateTime
) {
    companion object {
        fun of(
            request: MessageDto.Request,
            member: Member,
            chatRoom: ChatRoom
        ) = Chat(
            content = request.message,
            member = member,
            chatRoom = chatRoom,
            sendAt = LocalDateTime.now()
        )
    }
}
