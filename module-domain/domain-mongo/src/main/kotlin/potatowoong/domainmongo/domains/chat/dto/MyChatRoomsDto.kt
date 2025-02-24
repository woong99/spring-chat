package potatowoong.domainmongo.domains.chat.dto

import java.time.LocalDateTime

data class MyChatRoomsDto(
    val chatRoomId: String,
    val lastMessage: String?,
    val lastSendAt: LocalDateTime?,
    val chatRoomName: String,
    val participantCount: Long,
    var unreadMessageCount: Long
) {
    fun markAllMessageAsRead() {
        this.unreadMessageCount = 0
    }
}
