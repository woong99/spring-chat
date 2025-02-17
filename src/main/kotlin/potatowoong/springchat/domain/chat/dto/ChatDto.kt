package potatowoong.springchat.domain.chat.dto

import potatowoong.springchat.domain.chat.entity.ChatMessage
import java.time.ZoneOffset

class ChatDto {

    data class Response(
        val chatRoomName: String,
        val messages: List<Message>
    ) {
        companion object {
            fun of(
                chatRoomName: String,
                nicknameMap: Map<Long, String>,
                messages: List<ChatMessage>
            ) = Response(
                chatRoomName = chatRoomName,
                messages = messages.map {
                    Message.of(
                        nicknameMap[it.memberId] ?: "",
                        it,
                    )
                }
            )
        }

        data class Message(
            val sender: Long,
            val nickname: String,
            val message: String,
            val sendAt: Long,
        ) {
            companion object {
                fun of(
                    nickname: String,
                    chat: ChatMessage,
                ) = Message(
                    sender = chat.memberId,
                    nickname = nickname,
                    message = chat.content,
                    sendAt = chat.sendAt.toInstant(ZoneOffset.ofHours(9)).toEpochMilli(),
                )
            }
        }
    }
}