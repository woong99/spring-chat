package potatowoong.domainwebsocket.chat.dto

import potatowoong.domainwebsocket.chat.enums.ChatCommand
import potatowoong.modulesecurity.auth.data.CustomUserDetails

class MessageDto {
    data class Request(
        val message: String,
        val type: ChatCommand
    )

    data class Message(
        val sender: Long,
        val nickname: String,
        val message: String,
        val sendAt: Long,
        val chatRoomId: String
    ) {
        companion object {
            fun of(
                userDetails: CustomUserDetails,
                message: String,
                chatRoomId: String
            ) = Message(
                sender = userDetails.id,
                nickname = userDetails.nickname,
                message = message,
                sendAt = System.currentTimeMillis(),
                chatRoomId = chatRoomId
            )
        }
    }
}
