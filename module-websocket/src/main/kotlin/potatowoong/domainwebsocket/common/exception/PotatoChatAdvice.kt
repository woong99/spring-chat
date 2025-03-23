package potatowoong.domainwebsocket.common.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestControllerAdvice
import potatowoong.domainwebsocket.chat.dto.MessageDto
import potatowoong.domainwebsocket.config.security.StompCustomUserDetails

@RestControllerAdvice
class PotatoChatAdvice(
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    private val log = KotlinLogging.logger { }

    @MessageExceptionHandler
    fun handleException(
        authentication: Authentication,
        e: Exception,
        stompHeaderAccessor: StompHeaderAccessor
    ) {
        log.error(e) { "MessageExceptionHandler" }

        // 인증 정보
        val userDetails = authentication.principal as StompCustomUserDetails

        simpMessagingTemplate.convertAndSendToUser(
            userDetails.stompSessionId,
            "/error",
            MessageDto.Request.createErrorMessage(),
        )
    }
}