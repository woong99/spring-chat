package potatowoong.modulesse.notification.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NotificationDto(
    @JsonProperty("sender") val sender: Long,
    @JsonProperty("chatRoomId") val chatRoomId: String,
    @JsonProperty("message") val message: String
) {
}
