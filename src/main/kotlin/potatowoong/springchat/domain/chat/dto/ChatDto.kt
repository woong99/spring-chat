package potatowoong.springchat.domain.chat.dto

import potatowoong.springchat.domain.chat.entity.ChatMessage
import java.time.ZoneOffset

class ChatDto {

    data class Response(
        val chatRoomName: String,
        val messages: List<Message>,
        val hasMore: Boolean
    ) {
        companion object {
            fun of(
                chatRoomName: String,
                nicknameMap: Map<Long, String>,
                messages: List<ChatMessage>
            ) = Response(
                chatRoomName = chatRoomName,
                messages = messages.take(50).map {
                    Message.of(
                        nicknameMap[it.memberId] ?: "",
                        it,
                    )
                },
                hasMore = messages.size > 50
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