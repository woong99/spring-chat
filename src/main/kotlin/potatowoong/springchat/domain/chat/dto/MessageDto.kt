package potatowoong.springchat.domain.chat.dto

import potatowoong.springchat.domain.chat.entity.Chat
import java.time.ZoneOffset

class MessageDto {
    data class Request(
        val message: String,
    )

    data class Response(
        val sender: String,
        val message: String,
        val sendAt: Long
    ) {
        companion object {
            fun of(
                sender: String,
                message: String,
            ) = Response(
                sender = sender,
                message = message,
                sendAt = System.currentTimeMillis()
            )

            fun of(
                chat: Chat
            ) = Response(
                sender = chat.member.nickname,
                message = chat.content,
                sendAt = chat.sendAt.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
            )
        }
    }
}
