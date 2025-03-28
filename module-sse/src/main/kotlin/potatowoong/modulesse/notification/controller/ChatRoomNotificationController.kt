package potatowoong.modulesse.notification.controller

import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import potatowoong.modulesse.notification.service.ChatRoomNotificationService
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/v1/chat-room/notification")
class ChatRoomNotificationController(
    private val chatRoomNotificationService: ChatRoomNotificationService
) {

    /**
     * 채팅방 실시간 갱신을 위한 구독 API
     */
    @GetMapping(value = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribe(): Flux<ServerSentEvent<Any>> {
        return chatRoomNotificationService.connect()
    }
}