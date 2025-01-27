package potatowoong.springchat.domain.chat.controller

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import potatowoong.springchat.domain.auth.service.AuthService
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.service.ChatRoomNotificationService
import potatowoong.springchat.domain.chat.service.ChatService

@RestController
class StompController(
    private val rabbitTemplate: RabbitTemplate,
    private val chatService: ChatService,
    private val authService: AuthService,
    private val chatRoomNotificationService: ChatRoomNotificationService,
) {
    
    @MessageMapping("chat.message.{chatRoomId}")
    fun chat(
        @DestinationVariable chatRoomId: String,
        request: MessageDto.Request,
        authentication: Authentication
    ) {
        // 닉네임 조회
        val nickname = authService.getMyInfo(authentication.name.toLong()).nickname

        // 채팅 저장
        chatService.saveChat(
            chatRoomId,
            request,
            authentication.name.toLong()
        )

        // 메시지 전송
        rabbitTemplate.convertAndSend(
            "chat.exchange",
            "chat.room.${chatRoomId}",
            MessageDto.Response.Message.of(nickname, request.message)
        )

        // 채팅방 실시간 갱신
        chatRoomNotificationService.sendToClient(
            chatRoomId,
            request.message
        )
    }
}
