package potatowoong.domainwebsocket.chat.components

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import potatowoong.domainwebsocket.chat.dto.MessageDto
import potatowoong.domainwebsocket.chat.dto.NotificationDto
import potatowoong.domainwebsocket.config.kafka.KafkaConstants

@Component
class KafkaChatConsumer(
    private val objectMapper: ObjectMapper,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    @KafkaListener(topics = [KafkaConstants.CHAT_TOPIC], groupId = "\${spring.kafka.consumer.group-id}")
    fun consume(message: String) {
        val messageDto = objectMapper.readValue(message, MessageDto.Message::class.java)

        simpMessagingTemplate.convertAndSend(
            "/sub/${messageDto.chatRoomId}",
            messageDto
        )

        // 채팅방 실시간 갱신
        kafkaTemplate.send(
            KafkaConstants.NOTIFICATION_TOPIC,
            objectMapper.writeValueAsString(
                NotificationDto.of(
                    messageDto.chatRoomId,
                    messageDto.message
                )
            )
        )
    }
}