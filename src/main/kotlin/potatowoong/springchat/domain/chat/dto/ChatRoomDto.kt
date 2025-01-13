package potatowoong.springchat.domain.chat.dto

import jakarta.validation.constraints.NotBlank
import potatowoong.springchat.domain.chat.entity.ChatRoom

class ChatRoomDto {

    data class Request(
        @field:NotBlank(message = "채팅방 이름은 필수입니다.")
        val name: String
    )

    data class Response(
        val chatRoomId: String,
        val name: String
    ) {
        companion object {
            fun of(
                chatRoom: ChatRoom
            ) = Response(
                chatRoomId = chatRoom.chatRoomId!!,
                name = chatRoom.name
            )
        }
    }
}