package potatowoong.springchat.domain.chat.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

class ChatRoomDto {

    data class Request(
        @field:NotBlank(message = "채팅방 이름은 필수입니다.")
        val name: String
    )

    data class Response(
        val chatRoomId: String,
        val name: String,
        val lastChatMessage: String?,
        @field:JsonIgnore val lastChatSendAtTime: LocalDateTime?
    ) {
        val lastChatSendAt: Long? = lastChatSendAtTime?.toInstant(java.time.ZoneOffset.ofHours(9))?.toEpochMilli()
    }
}
