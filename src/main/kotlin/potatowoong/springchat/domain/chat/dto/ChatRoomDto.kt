package potatowoong.springchat.domain.chat.dto

import jakarta.validation.constraints.NotBlank

class ChatRoomDto {

    data class Request(
        @field:NotBlank(message = "채팅방 이름은 필수입니다.")
        val name: String
    )
}
