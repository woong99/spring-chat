package potatowoong.springchat.domain.chat.components

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent

@Component
class WebSocketEventListener {

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
    }
}
