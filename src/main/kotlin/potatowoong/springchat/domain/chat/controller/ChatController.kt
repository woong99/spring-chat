package potatowoong.springchat.domain.chat.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import potatowoong.springchat.domain.auth.repository.MemberRepository
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode

@RestController
class ChatController(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val memberRepository: MemberRepository
) {
    private val log = KotlinLogging.logger { }

    @MessageMapping("/chat")
    fun chat(
        message: String,
        authentication: Authentication
    ) {
        log.info { "message : $message, auth : ${authentication.name}" }
        val member = memberRepository.findByIdOrNull(authentication.name.toLong())
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        simpMessagingTemplate.convertAndSend("/sub/1", MessageDto.of(member.nickname, message))
    }
}
