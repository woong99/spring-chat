package potatowoong.moduleapi.api.chat.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import potatowoong.domainmongo.domains.chat.dto.MyChatRoomsDto
import potatowoong.moduleapi.api.chat.dto.AllChatRoomsDto
import potatowoong.moduleapi.api.chat.dto.ChatRoomDto
import potatowoong.moduleapi.api.chat.service.ChatRoomService
import potatowoong.moduleapi.common.api.ApiResponse

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
