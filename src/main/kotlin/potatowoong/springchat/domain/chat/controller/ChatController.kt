package potatowoong.springchat.domain.chat.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import potatowoong.springchat.domain.chat.dto.ChatDto
import potatowoong.springchat.domain.chat.service.ChatService
import potatowoong.springchat.global.common.ApiResponse

@RestController
@RequestMapping("/api/v1/chat")
class ChatController(
    private val chatService: ChatService,
) {
    /**
     * 채팅 내역 조회
     */
    @GetMapping("/{chatRoomId}/messages")
    fun getChatList(
        @PathVariable chatRoomId: String
    ): ResponseEntity<ApiResponse<ChatDto.Response>> {
        return ApiResponse.success(chatService.getChatList(chatRoomId))
    }
}
