package potatowoong.moduleapi.api.chat.dto

import io.github.oshai.kotlinlogging.KotlinLogging
import potatowoong.domainmongo.domains.chat.entity.Chat
import potatowoong.domainmongo.domains.chat.entity.ChatRoom
import potatowoong.domainmongo.domains.chat.enums.ChatRoomType
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesecurity.utils.SecurityUtils
import java.time.ZoneOffset

class ChatDto {

    data class Response(
        val chatRoomName: String,
        val messages: List<Message>,
        val hasMore: Boolean
    ) {
        companion object {
            private val log = KotlinLogging.logger { }

            fun of(
                chatRoom: ChatRoom,
                nicknameMap: Map<Long, String>,
                messages: List<Chat>
            ) = Response(
                chatRoomName = getChatRoomName(chatRoom, nicknameMap),
                messages = messages.take(50).map {
                    Message.of(
                        nicknameMap[it.memberId] ?: "",
                        it,
                    )
                },
                hasMore = messages.size > 50
            )

            /**
             * 채팅방 이름 반환
             */
            private fun getChatRoomName(
                chatRoom: ChatRoom,
                nicknameMap: Map<Long, String>
            ): String {
                return if (chatRoom.chatRoomType == ChatRoomType.GROUP) { // 그룹 채팅방
                    chatRoom.name ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
                } else { // 1:1 채팅방
                    if (nicknameMap.size != 2) {
                        log.error { "1:1 채팅방 멤버 수가 2명이 아닙니다. chatRoomId=${chatRoom.id}, size=${nicknameMap.size}" }
                        throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
                    }

                    // 내 정보를 제외한 상대방 닉네임
                    nicknameMap.filterKeys { it != SecurityUtils.getCurrentUserId() }.values.firstOrNull()
                        ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
                }
            }
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
                    chat: Chat,
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