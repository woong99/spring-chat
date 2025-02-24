package potatowoong.modulesse.notification.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NotificationDto(
    @JsonProperty("chatRoomId") val chatRoomId: String,
    @JsonProperty("message") val message: String
) {
    companion object {
        fun of(
            chatRoomId: String,
            message: String
        ): NotificationDto = NotificationDto(
            chatRoomId = chatRoomId,
            message = message
        )
    }
}
