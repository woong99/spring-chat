package potatowoong.domainmongo.domains.chat.dto

import potatowoong.domainmongo.domains.chat.entity.ChatRoom

data class ChatRoomIdDto(
    val chatRoomId: String
) {
    companion object {
        fun of(chatRoom: ChatRoom) = ChatRoomIdDto(
            chatRoomId = chatRoom.id.toString()
        )
    }
}