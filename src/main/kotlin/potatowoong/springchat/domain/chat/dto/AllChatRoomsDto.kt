package potatowoong.springchat.domain.chat.dto

import java.time.LocalDateTime

data class AllChatRoomsDto(
    val chatRoomId: String,
    val chatRoomName: String,
    val participantCount: Long,
) {
    private var lastSendAt: LocalDateTime? = null

    fun updateLastSendAt(sendAt: LocalDateTime) {
        lastSendAt = sendAt
    }
}
