package potatowoong.domainmongo.domains.chat.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document(collection = "chat_room_member")
class ChatRoomMember(

    @MongoId
    val id: ObjectId? = null,

    val chatRoomId: ObjectId,

    val memberId: Long,

    val firstJoinedAt: LocalDateTime = LocalDateTime.now(),

    val lastJoinedAt: LocalDateTime? = null
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
}