package potatowoong.springchat.domain.chat.repository

import potatowoong.springchat.domain.chat.entity.Chat

interface ChatRepositoryCustom {

    fun findMessagesWithPaging(chatRoomId: String, page: Long): List<Chat>
}