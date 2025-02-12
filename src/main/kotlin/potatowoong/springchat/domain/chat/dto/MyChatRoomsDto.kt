package potatowoong.springchat.domain.chat.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import potatowoong.springchat.domain.chat.entity.ChatMessage
import java.time.LocalDateTime
import java.time.ZoneOffset

data class MyChatRoomsDto(
    val chatRoomId: String,
    val chatRoomName: String,
    @field:JsonIgnore val lastJoinedAt: LocalDateTime,
    val participantCount: Long
) {
    @field:JsonProperty("lastChatMessage")
    private var lastChatMessage: String? = null

    @field:JsonProperty("lastSendAt")
    var lastSendAt: Long? = null
        private set

    @field:JsonProperty("unreadCount")
    var unreadCount: Long = 0
        private set

    fun updateLastChatMessage(chatMessage: ChatMessage) {
        lastChatMessage = chatMessage.content
        lastSendAt = chatMessage.sendAt.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
    }

    fun updateUnreadCount(unreadCount: Long) {
        this.unreadCount = unreadCount
    }
}
