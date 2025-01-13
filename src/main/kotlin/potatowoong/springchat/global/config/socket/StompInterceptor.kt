package potatowoong.springchat.global.config.socket

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import potatowoong.springchat.global.auth.jwt.components.JwtTokenProvider
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode

@Component
class StompInterceptor(
    private val jwtTokenProvider: JwtTokenProvider
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        // CONNECT 메시지인 경우 토큰을 추출하여 인증 정보를 설정
        if (accessor?.command == StompCommand.CONNECT) {
            val headers = accessor.getNativeHeader("Authorization")

            accessor.user = getAuthentication(headers)
        }
        return super.preSend(message, channel)
    }

    private fun getAuthentication(
        headers: List<String>?
    ): Authentication {
        val token = headers?.firstOrNull()
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        val jwtToken = token.substring(7)

        return try {
            jwtTokenProvider.getAuthentication(jwtToken)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.UNAUTHORIZED)
        }
    }
}
