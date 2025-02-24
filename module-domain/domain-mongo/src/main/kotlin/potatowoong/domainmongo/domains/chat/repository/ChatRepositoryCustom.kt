package potatowoong.domainmongo.domains.chat.repository

import potatowoong.domainmongo.domains.chat.entity.Chat

interface ChatRepositoryCustom {

    fun findMessagesWithPaging(chatRoomId: String, page: Long): List<Chat>
}