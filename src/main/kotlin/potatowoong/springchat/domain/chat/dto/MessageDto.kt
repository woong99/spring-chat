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
        val createdAt: Long
    ) {
        companion object {
            fun of(
                sender: String,
                message: String,
            ) = Response(
                sender = sender,
                message = message,
                createdAt = System.currentTimeMillis()
            )

            fun of(
                chat: Chat
            ) = Response(
                sender = chat.member.nickname,
                message = chat.content,
                createdAt = chat.createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()
            )
        }
    }
}