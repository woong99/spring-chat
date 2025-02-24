package potatowoong.domainmongo.domains.chat.entity

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "chat_room")
class ChatRoom(

    @MongoId
    val id: ObjectId? = null,

    val name: String,
) {
}