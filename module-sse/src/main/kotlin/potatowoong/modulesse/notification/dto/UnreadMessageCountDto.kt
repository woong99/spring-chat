package potatowoong.modulesse.notification.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class UnreadMessageCountDto(
    val chatRoomId: String,
    val unreadMessageCount: Int,
    val lastMessage: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val lastSendAt: LocalDateTime
) {
}