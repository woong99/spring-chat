package potatowoong.moduleapi.api.chat.dto

import potatowoong.domainmongo.domains.chat.entity.Chat
import potatowoong.domainrdb.domains.auth.entity.Member
import java.time.ZoneOffset

class ChatDto {

    data class Response(
        val data: List<Message>,
        val hasMore: Boolean,
        val page: Int
    ) {
        companion object {
            fun of(
                nicknameMap: Map<Long, Member>,
                messages: List<Chat>,
                page: Int
            ) = Response(
                data = messages.take(50).map {
                    val member = nicknameMap[it.memberId]
                    Message.of(
                        member?.nickname ?: "",
                        member?.profileImageUrl,
                        it,
                    )
                },
                hasMore = messages.size > 50,
                page = page
            )
        }

        data class Message(
            val sender: Long,
            val nickname: String,
            val message: String,
            val profileImageUrl: String?,
            val sendAt: Long,
        ) {
            companion object {
                fun of(
                    nickname: String,
                    profileImageUrl: String?,
                    chat: Chat,
                ) = Message(
                    sender = chat.memberId,
                    nickname = nickname,
                    message = chat.content,
                    profileImageUrl = profileImageUrl,
                    sendAt = chat.sendAt.toInstant(ZoneOffset.ofHours(9)).toEpochMilli(),
                )
            }
        }
    }
}