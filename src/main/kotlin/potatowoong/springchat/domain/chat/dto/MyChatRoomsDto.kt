package potatowoong.springchat.domain.chat.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime

data class MyChatRoomsDto(
    val chatRoomId: String,
    val chatRoomName: String,
    @field:JsonIgnore val lastSendAtTime: LocalDateTime?,
    val participantCount: Long,
    val lastChatMessage: String?
) {
    val lastSendAt: Long? = lastSendAtTime?.toInstant(java.time.ZoneOffset.ofHours(9))?.toEpochMilli()
}
