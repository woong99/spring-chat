package potatowoong.springchat.domain.chat.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import potatowoong.springchat.domain.auth.service.AuthService
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.service.ChatService

@RestController
class StompController(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService,
    private val authService: AuthService
) {
    private val log = KotlinLogging.logger { }

    @MessageMapping("/chat/{chatRoomId}")
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
        simpMessagingTemplate.convertAndSend(
            "/sub/${chatRoomId}",
            MessageDto.Response.of(nickname, request.message)
        )
    }

}