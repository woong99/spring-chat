package potatowoong.springchat.domain.chat.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController(
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    private val log = KotlinLogging.logger { }

    @MessageMapping("/chat")
    fun chat(
        message: String
    ) {
        log.info { message }
        simpMessagingTemplate.convertAndSend("/sub/1", message)
    }
}
