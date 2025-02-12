package potatowoong.springchat.domain.chat.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import potatowoong.springchat.domain.auth.data.CustomUserDetails
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.service.ChatRoomNotificationService
import potatowoong.springchat.domain.chat.service.ChatService

@RestController
class StompController(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService,
    private val chatRoomNotificationService: ChatRoomNotificationService
) {
    private val log = KotlinLogging.logger { }

    @MessageMapping("/chat/{chatRoomId}")
    fun chat(
        @DestinationVariable chatRoomId: String,
        request: MessageDto.Request,
        authentication: Authentication
    ) {
        // 인증 정보
        val userDetails = authentication.principal as CustomUserDetails

        // 채팅 저장
        chatService.saveChat(
            chatRoomId,
            request,
            userDetails.id
        )

        // 메시지 전송
        simpMessagingTemplate.convertAndSend(
            "/sub/${chatRoomId}",
            MessageDto.Response.Message.of(
                userDetails = userDetails,
                message = request.message
            )
        )

        // 채팅방 실시간 갱신
        chatRoomNotificationService.sendToClient(
            chatRoomId,
            request.message
        )
    }
}
