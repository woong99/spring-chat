package potatowoong.moduleapi.api.chat.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import potatowoong.moduleapi.api.chat.dto.ChatDto
import potatowoong.moduleapi.api.chat.service.ChatService
import potatowoong.moduleapi.common.api.ApiResponse

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
        @PathVariable chatRoomId: String,
        @RequestParam("page", defaultValue = "0") page: Int
    ): ResponseEntity<ApiResponse<ChatDto.Response>> {
        return ApiResponse.success(chatService.getChatList(chatRoomId, page))
    }
}
