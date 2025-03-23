package potatowoong.domainwebsocket.chat.dto

import potatowoong.domainwebsocket.chat.enums.ChatCommand
import potatowoong.domainwebsocket.config.security.StompCustomUserDetails

class MessageDto {
    data class Request(
        val message: String,
        val type: ChatCommand
    ) {
        companion object {
            fun ofClose() = Request(
                message = "SHUTDOWN",
                type = ChatCommand.SHUTDOWN
            )

            fun createErrorMessage() = Request(
                message = "ERROR",
                type = ChatCommand.ERROR
            )
        }
    }

    data class Message(
        val sender: Long,
        val nickname: String,
        val profileImageUrl: String? = null,
        val message: String,
        val sendAt: Long,
        val chatRoomId: String
    ) {
        companion object {
            fun of(
                userDetails: StompCustomUserDetails,
                message: String,
                chatRoomId: String
            ) = Message(
                sender = userDetails.id,
                nickname = userDetails.nickname,
                profileImageUrl = userDetails.profileImageUrl,
                message = message,
                sendAt = System.currentTimeMillis(),
                chatRoomId = chatRoomId
            )
        }
    }
}
