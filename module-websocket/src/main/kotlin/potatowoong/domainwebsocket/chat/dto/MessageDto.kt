package potatowoong.domainwebsocket.chat.dto

import potatowoong.domainwebsocket.chat.enums.ChatCommand
import potatowoong.modulesecurity.auth.data.CustomUserDetails

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

            fun of(
                chatRoomId: String
            ) = Message(
                sender = 999,
                nickname = "test",
                message = "오류",
                sendAt = System.currentTimeMillis(),
                chatRoomId = chatRoomId
            )
        }
    }
}
