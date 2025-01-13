package potatowoong.springchat.domain.chat.dto

import jakarta.validation.constraints.NotBlank
import potatowoong.springchat.domain.chat.entity.ChatRoom

class ChatRoomDto {

    data class Request(
        @field:NotBlank(message = "채팅방 이름은 필수입니다.")
        val name: String
    )

    data class Response(
        val id: String,
        val name: String
    ) {
        companion object {
            fun of(
                chatRoom: ChatRoom
            ) = Response(
                id = chatRoom.id!!,
                name = chatRoom.name
            )
        }
    }
}