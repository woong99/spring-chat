package potatowoong.domainwebsocket.config.socket

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.messaging.MessageDeliveryException
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler
import potatowoong.modulecommon.exception.CommonErrorCode
import potatowoong.modulecommon.exception.CustomException

@Component
class StompErrorHandler : StompSubProtocolErrorHandler() {

    private val log = KotlinLogging.logger { }

    override fun handleClientMessageProcessingError(
        clientMessage: Message<ByteArray>?,
        ex: Throwable
    ): Message<ByteArray>? {
        log.error { "handleClientMessageProcessingError: $ex" }
        if (ex is MessageDeliveryException && ex.cause is CustomException) {
            return sendErrorMessage((ex.cause as CustomException).errorCode)
        }
        return super.handleClientMessageProcessingError(clientMessage, ex)
    }

    private fun sendErrorMessage(
        errorCode: CommonErrorCode
    ): Message<ByteArray> {
        val headers = StompHeaderAccessor.create(StompCommand.ERROR)

        return MessageBuilder.createMessage(errorCode.errorCode.toByteArray(), headers.messageHeaders)
    }
}
