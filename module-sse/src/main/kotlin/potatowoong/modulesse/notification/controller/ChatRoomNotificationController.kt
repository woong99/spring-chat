package potatowoong.modulesse.notification.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import potatowoong.modulesse.notification.service.ChatRoomNotificationService

@RestController
@RequestMapping("/api/v1/chat-room/notification")
class ChatRoomNotificationController(
    private val chatRoomNotificationService: ChatRoomNotificationService
) {

    /**
     * 채팅방 실시간 갱신을 위한 구독 API
     */
    @GetMapping(value = ["/subscribe"], produces = ["text/event-stream;charset=UTF-8"])
    fun subscribe(): SseEmitter {
        return chatRoomNotificationService.subscribe()
    }
}