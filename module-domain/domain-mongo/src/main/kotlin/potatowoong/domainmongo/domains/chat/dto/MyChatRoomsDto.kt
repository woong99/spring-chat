package potatowoong.domainmongo.domains.chat.dto

import potatowoong.domainmongo.domains.chat.enums.ChatRoomType
import java.time.LocalDateTime

data class MyChatRoomsDto(
    val chatRoomId: String,
    val chatRoomType: ChatRoomType,
    val lastMessage: String?,
    val lastSendAt: LocalDateTime?,
    val chatRoomName: String?,
    val participantCount: Long,
    var unreadMessageCount: Long,
    val memberIds: List<Long>
) {
    fun markAllMessageAsRead() {
        this.unreadMessageCount = 0
    }
}
