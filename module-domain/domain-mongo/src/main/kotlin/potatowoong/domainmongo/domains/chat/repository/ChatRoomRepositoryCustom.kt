package potatowoong.domainmongo.domains.chat.repository

import potatowoong.domainmongo.domains.chat.dto.ChatRoomIdDto
import potatowoong.domainmongo.domains.chat.dto.MyChatRoomsDto

interface ChatRoomRepositoryCustom {

    fun findMyChatRooms(memberId: Long): List<MyChatRoomsDto>

    fun findPrivateChatRoomId(friendId: Long): ChatRoomIdDto?
}