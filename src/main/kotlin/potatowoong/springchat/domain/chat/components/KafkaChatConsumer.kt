package potatowoong.springchat.domain.chat.components

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.global.config.kafka.KafkaConstants

@Component
class KafkaChatConsumer(
    private val objectMapper: ObjectMapper,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    @KafkaListener(topics = [KafkaConstants.CHAT_TOPIC], groupId = "\${spring.kafka.consumer.group-id}")
    fun consume(message: String) {
        val messageDto = objectMapper.readValue(message, MessageDto.Message::class.java)

        simpMessagingTemplate.convertAndSend(
            "/sub/${messageDto.chatRoomId}",
            messageDto
        )
    }
}