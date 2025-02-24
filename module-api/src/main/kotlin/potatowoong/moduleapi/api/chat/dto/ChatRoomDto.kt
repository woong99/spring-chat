package potatowoong.moduleapi.api.chat.dto

import jakarta.validation.constraints.NotBlank
import potatowoong.domainmongo.domains.chat.entity.ChatRoom

class ChatRoomDto {

    data class Request(
        @field:NotBlank(message = "채팅방 이름은 필수입니다.")
        val name: String
    ) {
        fun toEntity() = ChatRoom(
            name = this.name
        )
    }
}
