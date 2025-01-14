package potatowoong.springchat.domain.chat.components

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import potatowoong.springchat.domain.chat.service.ChatRoomService

@Component
class WebSocketEventListener(
    private val chatRoomService: ChatRoomService
) {

    private val log = KotlinLogging.logger { }

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        log.info { "New WebSocket Connection" }
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        log.info { "WebSocket Disconnected" }
    }

    @EventListener
    fun handleWebSocketSubscribeListener(event: SessionSubscribeEvent) {
        log.info { "WebSocket Subscribe : ${event.message}" }

        val memberId = event.user?.name?.toLongOrNull()
        val chatRoomId = event.message.headers["simpDestination"].toString().split("/").lastOrNull()

        // 채팅방 입장 처리
        if (memberId != null && !chatRoomId.isNullOrBlank()) {
            chatRoomService.enterChatRoom(memberId, chatRoomId)
        }
    }
}
