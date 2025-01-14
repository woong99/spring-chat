package potatowoong.springchat.domain.chat.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import potatowoong.springchat.domain.chat.dto.AllChatRoomsDto
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.domain.chat.dto.MyChatRoomsDto
import potatowoong.springchat.domain.chat.service.ChatRoomService
import potatowoong.springchat.global.common.ApiResponse

@RestController
@RequestMapping("/api/v1/chat-room")
class ChatRoomController(
    private val chatRoomService: ChatRoomService
) {

    /**
     * 채팅방 생성 API
     */
    @PostMapping
    fun addChatRoom(
        @Valid @RequestBody request: ChatRoomDto.Request
    ): ResponseEntity<ApiResponse<Unit>> {
        chatRoomService.addChatRoom(request)

        return ApiResponse.success()
    }

    /**
     * 전체 채팅방 목록 조회
     */
    @GetMapping("/all-list")
    fun getAllChatRooms(): ResponseEntity<ApiResponse<List<AllChatRoomsDto>>> {
        return ApiResponse.success(chatRoomService.getAllChatRooms())
    }

    /**
     * 채팅방 목록 조회 API
     */
    @GetMapping("/my-list")
    fun getChatRooms(): ResponseEntity<ApiResponse<List<MyChatRoomsDto>>> {
        return ApiResponse.success(chatRoomService.getMyChatRooms())
    }
}
