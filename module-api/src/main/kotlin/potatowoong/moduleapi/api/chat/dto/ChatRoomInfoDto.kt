package potatowoong.moduleapi.api.chat.dto

import potatowoong.domainmongo.domains.chat.entity.ChatRoom
import potatowoong.domainmongo.domains.chat.enums.ChatRoomType
import potatowoong.domainrdb.domains.auth.dto.MemberWithFriendshipStatusDto
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesecurity.utils.SecurityUtils

data class ChatRoomInfoDto(
    val chatRoomId: String,
    val chatRoomName: String,
    val chatRoomType: ChatRoomType,
    val users: List<MemberWithFriendshipStatusDto>
) {
    companion object {
        fun of(
            chatRoom: ChatRoom,
            users: List<MemberWithFriendshipStatusDto>
        ) = ChatRoomInfoDto(
            chatRoomId = chatRoom.id.toString(),
            chatRoomName = if (chatRoom.chatRoomType == ChatRoomType.GROUP) {
                chatRoom.name ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
            } else {
                users.firstOrNull { it.id != SecurityUtils.getCurrentUserId() }?.nickname
                    ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
            },
            chatRoomType = chatRoom.chatRoomType,
            users = users
        )
    }
}