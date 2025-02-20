package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.Id
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat")
class Chat(

    @Id
    val id: ObjectId? = null,

    val content: String,

    val memberId: Long,

    val chatRoomId: ObjectId,

    val sendAt: LocalDateTime = LocalDateTime.now(),
) {
}