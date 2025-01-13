package potatowoong.springchat.domain.chat.repository

import potatowoong.springchat.domain.chat.dto.ChatRoomDto

interface ChatRepositoryCustom {

    fun findChatRoomsWithLastChat(): List<ChatRoomDto.Response>
}
