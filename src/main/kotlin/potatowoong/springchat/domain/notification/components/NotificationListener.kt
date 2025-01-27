package potatowoong.springchat.domain.notification.components

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import potatowoong.springchat.domain.chat.service.ChatRoomNotificationService
import potatowoong.springchat.domain.notification.dto.NotificationDto

@Component
class NotificationListener(
    private val chatRoomNotificationService: ChatRoomNotificationService,
) {

    @RabbitListener(queues = ["notification.queue.\${notification.queue-number}"])
    fun receiveNotification(
        notificationDto: NotificationDto
    ) {
        chatRoomNotificationService.sendToClient(notificationDto.chatRoomId, notificationDto.message)
    }
}
