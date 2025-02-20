package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.Id
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_room_member")
class ChatRoomMember(
    @Id
    val id: String? = null,

    val chatRoomId: ObjectId,

    val memberId: Long,

    val firstJoinedAt: LocalDateTime = LocalDateTime.now(),

    var lastJoinedAt: LocalDateTime? = null
) {
    companion object {
        fun of(
            chatRoomId: ObjectId,
            memberId: Long
        ) = ChatRoomMember(
            chatRoomId = chatRoomId,
            memberId = memberId
        )
    }

    fun updateLastJoinedAt() {
        this.lastJoinedAt = LocalDateTime.now()
    }
}