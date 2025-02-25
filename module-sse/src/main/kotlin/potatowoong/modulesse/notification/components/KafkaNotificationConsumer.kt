package potatowoong.modulesse.notification.components

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import potatowoong.modulesse.config.kafka.KafkaConstants
import potatowoong.modulesse.notification.dto.NotificationDto
import potatowoong.modulesse.notification.service.ChatRoomNotificationService

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

        chatRoomNotificationService.sendUnreadMessageCount(notificationDto.chatRoomId, notificationDto.message)
    }
}
