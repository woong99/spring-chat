package potatowoong.domainmongo.domains.chat.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document(collection = "chat")
class Chat(

    @MongoId
    val id: ObjectId? = null,

    val content: String,

    val memberId: Long,

    val chatRoomId: ObjectId,

    val sendAt: LocalDateTime = LocalDateTime.now(),
) {
}