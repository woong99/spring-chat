package potatowoong.springchat.domain.chat.components

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import potatowoong.springchat.domain.chat.service.ChatRoomService

@Component
class WebSocketEventListener(
    private val chatRoomService: ChatRoomService
) {
    @EventListener
    fun handleWebSocketSubscribeListener(event: SessionSubscribeEvent) {
        val memberId = event.user?.name?.toLongOrNull()
        val chatRoomId = event.message.headers["simpDestination"].toString()
            .split("/")
            .lastOrNull()
            ?.split(".")
            ?.lastOrNull()

        // 채팅방 입장 처리
        if (memberId != null && !chatRoomId.isNullOrBlank()) {
            chatRoomService.enterChatRoom(memberId, chatRoomId)
        }
    }
}
