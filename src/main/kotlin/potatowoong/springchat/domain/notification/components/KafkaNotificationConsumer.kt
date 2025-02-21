package potatowoong.springchat.domain.notification.components

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import potatowoong.springchat.domain.chat.service.ChatRoomNotificationService
import potatowoong.springchat.domain.notification.dto.NotificationDto
import potatowoong.springchat.global.config.kafka.KafkaConstants

@Component
class KafkaNotificationConsumer(
    private val chatRoomNotificationService: ChatRoomNotificationService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = [KafkaConstants.NOTIFICATION_TOPIC], groupId = "\${spring.kafka.consumer.group-id}")
    fun receiveNotification(
        notification: String
    ) {
        val notificationDto = objectMapper.readValue(notification, NotificationDto::class.java)

        chatRoomNotificationService.sendToClient(notificationDto.chatRoomId, notificationDto.message)
    }
}
