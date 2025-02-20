package potatowoong.springchat.domain.chat.repository

import potatowoong.springchat.domain.chat.dto.MyChatRoomsDto

interface ChatRoomRepositoryCustom {

    fun findMyChatRooms(): List<MyChatRoomsDto>
}