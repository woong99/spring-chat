package potatowoong.springchat.domain.chat.controller

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import potatowoong.springchat.domain.auth.data.CustomUserDetails
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.service.ChatService
import potatowoong.springchat.domain.notification.dto.NotificationDto

@RestController
class StompController(
    private val rabbitTemplate: RabbitTemplate,
    private val chatService: ChatService,
) {

    @MessageMapping("chat.message.{chatRoomId}")
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
        rabbitTemplate.convertAndSend(
            "chat.exchange",
            "chat.room.${chatRoomId}",
            MessageDto.Response.Message.of(
                userDetails = userDetails,
                message = request.message
            )
        )

        // 채팅방 실시간 갱신
        rabbitTemplate.convertAndSend(
            "notification.exchange",
            "notification",
            NotificationDto.of(
                chatRoomId,
                request.message
            )
        )
    }
}
