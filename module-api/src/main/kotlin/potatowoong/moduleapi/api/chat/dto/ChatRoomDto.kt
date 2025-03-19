package potatowoong.moduleapi.api.chat.dto

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.NotBlank
import potatowoong.domainmongo.domains.chat.dto.MyChatRoomsDto
import potatowoong.domainmongo.domains.chat.entity.ChatRoom
import potatowoong.domainmongo.domains.chat.enums.ChatRoomType
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesecurity.utils.SecurityUtils
import java.time.LocalDateTime

class ChatRoomDto {

    data class Request(
        @field:NotBlank(message = "채팅방 이름은 필수입니다.")
        val name: String
    ) {
        fun toEntity() = ChatRoom(
            name = this.name,
            chatRoomType = ChatRoomType.GROUP
        )
    }

    data class Response(
        val chatRoomId: String,
        val chatRoomType: ChatRoomType,
        val lastMessage: String?,
        val lastSendAt: LocalDateTime?,
        val chatRoomName: String,
        val participantCount: Long,
        var unreadMessageCount: Long,
    ) {
        companion object {
            private val log = KotlinLogging.logger { }

            fun of(
                myChatRoomDto: MyChatRoomsDto,
                friendNicknameMap: Map<Long, String>
            ) = Response(
                chatRoomId = myChatRoomDto.chatRoomId,
                chatRoomType = myChatRoomDto.chatRoomType,
                lastMessage = myChatRoomDto.lastMessage,
                lastSendAt = myChatRoomDto.lastSendAt,
                chatRoomName = getChatRoomName(myChatRoomDto, friendNicknameMap),
                participantCount = myChatRoomDto.participantCount,
                unreadMessageCount = myChatRoomDto.unreadMessageCount
            )

            private fun getChatRoomName(
                myChatRoomDto: MyChatRoomsDto,
                friendNicknameMap: Map<Long, String>
            ): String {
                return if (myChatRoomDto.chatRoomType == ChatRoomType.GROUP) {
                    myChatRoomDto.chatRoomName ?: run {
                        log.error { "채팅방 이름이 없습니다. chatRoomId: ${myChatRoomDto.chatRoomId}" }
                        throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
                    }
                } else {
                    if (myChatRoomDto.memberIds.size != 2) {
                        log.error { "1:1 채팅방의 참여자 수가 2명이 아닙니다. chatRoomId: ${myChatRoomDto.chatRoomId}" }
                        throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
                    }

                    friendNicknameMap.filterKeys { it != SecurityUtils.getCurrentUserId() }.values.firstOrNull()
                        ?: kotlin.run {
                            log.error { "친구 정보가 없습니다. friendId: ${myChatRoomDto.memberIds.first()}, chatRoomId: ${myChatRoomDto.chatRoomId}" }
                            throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
                        }
                }
            }
        }
    }
}
