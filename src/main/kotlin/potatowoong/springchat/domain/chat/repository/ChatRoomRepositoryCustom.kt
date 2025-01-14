package potatowoong.springchat.domain.chat.repository

import potatowoong.springchat.domain.chat.dto.ChatRoomDto

interface ChatRoomRepositoryCustom {

    fun findChatRoomsWithLastChat(): List<ChatRoomDto.Response>
}
