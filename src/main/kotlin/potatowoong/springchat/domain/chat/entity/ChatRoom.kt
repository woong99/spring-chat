package potatowoong.springchat.domain.chat.entity

import jakarta.persistence.Id
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import potatowoong.springchat.domain.chat.dto.ChatRoomDto

@Document(collection = "chat_room")
class ChatRoom(
    @Id
    val id: ObjectId? = null,

    val name: String,
) {
    companion object {
        fun of(
            request: ChatRoomDto.Request
        ) = ChatRoom(
            name = request.name
        )
    }
}