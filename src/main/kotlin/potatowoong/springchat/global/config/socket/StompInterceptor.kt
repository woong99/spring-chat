package potatowoong.springchat.global.config.socket

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.enums.ChatCommand
import potatowoong.springchat.domain.chat.service.ChatRoomService
import potatowoong.springchat.global.auth.jwt.components.JwtTokenProvider
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode

@Component
class StompInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
    private val chatRoomService: ChatRoomService,
    private val objectMapper: ObjectMapper
) : ChannelInterceptor {
    
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (accessor?.command == StompCommand.CONNECT) {
            // CONNECT 메시지인 경우 토큰을 추출하여 인증 정보를 설정
            val headers = accessor.getNativeHeader("Authorization")

            accessor.user = getAuthentication(headers)
        } else if (accessor?.command == StompCommand.SEND) {
            val messageDto = parseMessageDto(message)

            // DISCONNECT 메시지인 경우 퇴장 처리
            if (messageDto.type == ChatCommand.DISCONNECT) {
                leaveChatRoom(accessor)
                return null;
            }
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

    /**
     * 메시지 파싱
     */
    private fun parseMessageDto(message: Message<*>): MessageDto.Request {
        val payload = when (message.payload) {
            is String -> message.payload as String
            is ByteArray -> String(message.payload as ByteArray)
            else -> throw IllegalArgumentException("Invalid payload type")
        }

        return objectMapper.readValue(payload, MessageDto.Request::class.java)
    }

    /**
     * 채팅방 퇴장 처리
     */
    private fun leaveChatRoom(accessor: StompHeaderAccessor) {
        val chatRoomId = accessor.getNativeHeader("destination")?.get(0)?.split("/")?.get(3)
            ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        val user = accessor.user ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        chatRoomService.leaveChatRoom(
            user.name.toLong(),
            chatRoomId
        )
    }
}
