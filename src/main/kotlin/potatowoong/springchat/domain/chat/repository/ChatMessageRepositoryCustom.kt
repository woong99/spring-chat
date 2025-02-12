package potatowoong.springchat.domain.chat.repository

import potatowoong.springchat.domain.chat.dto.LastMessageDto
import potatowoong.springchat.domain.chat.dto.MyChatRoomsDto
import potatowoong.springchat.domain.chat.entity.ChatMessage

interface ChatMessageRepositoryCustom {

    fun findLastMessages(chatRoomIds: List<String>): List<ChatMessage>

    fun findUnreadMessageCount(myChatRooms: List<MyChatRoomsDto>): List<LastMessageDto>
}