package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.*
import potatowoong.springchat.domain.auth.entity.Member
import java.time.LocalDateTime

@Entity
@Table(
    indexes = [
        Index(name = "idx_chat_room_member_chat_room_id", columnList = "chat_room_id"),
        Index(name = "idx_chat_room_member_member_id", columnList = "member_id")
    ]
)
class ChatRoomMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "chat_room_id",
        nullable = false,
        updatable = false,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val chatRoom: ChatRoom,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "member_id",
        nullable = false,
        updatable = false,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val member: Member,

    @Column(nullable = false, updatable = false)
    val firstJoinedAt: LocalDateTime,

    @Column(nullable = false)
    var lastJoinedAt: LocalDateTime
) {

    fun updateLastJoinedAt() {
        lastJoinedAt = LocalDateTime.now()
    }

    companion object {
        fun of(
            chatRoom: ChatRoom,
            member: Member
        ) = ChatRoomMember(
            chatRoom = chatRoom,
            member = member,
            firstJoinedAt = LocalDateTime.now(),
            lastJoinedAt = LocalDateTime.now()
        )
    }
}