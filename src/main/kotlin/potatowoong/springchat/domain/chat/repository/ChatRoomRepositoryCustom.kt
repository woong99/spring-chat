package potatowoong.springchat.domain.chat.repository

import potatowoong.springchat.domain.chat.dto.AllChatRoomsDto
import potatowoong.springchat.domain.chat.dto.MyChatRoomsDto

interface ChatRoomRepositoryCustom {

    fun findAllChatRooms(): List<AllChatRoomsDto>

    fun findMyChatRooms(): List<MyChatRoomsDto>
}
