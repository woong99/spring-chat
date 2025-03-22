package potatowoong.domainwebsocket.chat.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NotificationDto(
    @JsonProperty("sender") val sender: Long,
    @JsonProperty("chatRoomId") val chatRoomId: String,
    @JsonProperty("message") val message: String
) {
    companion object {
        fun of(
            sender: Long,
            chatRoomId: String,
            message: String
        ): NotificationDto = NotificationDto(
            sender = sender,
            chatRoomId = chatRoomId,
            message = message
        )
    }
}
