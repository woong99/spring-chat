package potatowoong.springchat.domain.chat.dto

import potatowoong.springchat.domain.chat.entity.Chat
import java.time.ZoneOffset

class MessageDto {
    data class Request(
        val message: String,
    )

    data class Response(
        val chatRoomName: String,
        val messages: List<Message>
    ) {
        companion object {
            fun of(
                chatRoomName: String,
                messages: List<Chat>
            ) = Response(
                chatRoomName = chatRoomName,
                messages = messages.map {
                    Message.of(it)
                }
            )
        }

        data class Message(
            val sender: String,
            val message: String,
            val sendAt: Long
        ) {
            companion object {
                fun of(
                    sender: String,
                    message: String,
                ) = Message(
                    sender = sender,
                    message = message,
                    sendAt = System.currentTimeMillis()
                )

                fun of(
                    chat: Chat
                ) = Message(
                    sender = chat.member.nickname,
                    message = chat.content,
                    sendAt = chat.sendAt.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
                )
            }
        }
    }
}
