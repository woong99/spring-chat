package potatowoong.springchat.domain.chat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import potatowoong.springchat.domain.auth.data.CustomUserDetails
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.service.ChatService
import potatowoong.springchat.global.config.kafka.KafkaConstants

@RestController
class StompController(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val chatService: ChatService,
    private val objectMapper: ObjectMapper
) {

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
        kafkaTemplate.send(
            KafkaConstants.CHAT_TOPIC,
            objectMapper.writeValueAsString(
                MessageDto.Message.of(
                    userDetails = userDetails,
                    message = request.message,
                    chatRoomId = chatRoomId
                )
            )
        )
    }
}
