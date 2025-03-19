package potatowoong.domainmongo.domains.chat.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import potatowoong.domainmongo.domains.chat.enums.ChatRoomType

@Document(collection = "chat_room")
class ChatRoom(

    @MongoId
    val id: ObjectId? = null,

    val name: String? = null,

    val chatRoomType: ChatRoomType
) {
    companion object {
        fun createPrivateRoom() = ChatRoom(
            chatRoomType = ChatRoomType.PRIVATE
        )
    }
}