package potatowoong.springchat.domain.chat.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import potatowoong.springchat.domain.chat.repository.ChatRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomMemberRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomRepository
import potatowoong.springchat.global.utils.SecurityUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatRoomNotificationService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val chatRepository: ChatRepository
) {

    private val log = KotlinLogging.logger { }

    private val emitters: MutableMap<Long, SseEmitter> = ConcurrentHashMap()

    private val sinks: MutableMap<Long, Sinks.Many<ServerSentEvent<Any>>> = ConcurrentHashMap()

    fun subscribe(): SseEmitter {
        val emitter = SseEmitter(1000 * 60 * 10)

        val memberId = SecurityUtils.getCurrentUserId()
        emitters[memberId] = emitter

        emitter.onCompletion {
            log.info { "subscribe - userId: $memberId, onCompletion" }
            emitters.remove(memberId)
        }

        emitter.onTimeout {
            emitters[memberId]?.complete()
            emitters.remove(memberId)
        }

        emitter.onError {
            log.error { "subscribe - userId: $memberId, error: ${it.message}" }
            emitters.remove(memberId)
        }

        sendToClient(memberId)
        return emitter
    }

    private fun sendToClient(
        userId: Long
    ) {
        try {
            log.info { "sendToClient - userId: $userId, emitters: $emitters" }
            val emitter = emitters[userId] ?: return
            emitter.send(
                SseEmitter.event()
                    .id(userId.toString())
                    .name("message")
                    .data("Hello, World!")
            )
        } catch (e: Exception) {
            log.error { "sendToClient - userId: $userId, error: ${e.message}" }
        }
    }

    fun sendToClient(
        chatRoomId: String,
        message: String
    ) {
        // 채팅방 조회
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId) ?: run {
            log.error { "sendToClient - chatRoomId: $chatRoomId, error: ChatRoom not found" }
            return
        }

        // 채팅방에 속한 사용자 조회
        val chatRoomMembers = chatRoomMemberRepository.findByChatRoomId(ObjectId(chatRoomId))
        val relevantChatRoomMembers = chatRoomMembers.filter { emitters.containsKey(it.memberId) }

        if (relevantChatRoomMembers.isNotEmpty()) {
            val topChats = chatRepository.findTop100ByChatRoomIdOrderBySendAtDesc(ObjectId(chatRoomId))

            chatRoomMembers.forEach { chatRoomMember ->
                val emitter = emitters[chatRoomMember.memberId] ?: return@forEach


                val unreadCount = topChats.count { chat -> !chat.sendAt.isBefore(chatRoomMember.lastJoinedAt) }
                println("unreadCount: $unreadCount")
            }
        }


//        // 채팅방의 최근 100개의 채팅 조회
//        val topChats = chatRepository.findTop100ByChatRoomChatRoomIdOrderBySendAtDesc(chatRoomId)
//
//        chatRoom.chatRoomMembers.forEach {
//            val emitter = emitters[it.member.id] ?: return@forEach
//
//            val unreadCount = topChats.count { chat -> !chat.sendAt.isBefore(it.lastJoinedAt) }
//
//            if (topChats.isNotEmpty()) {
//                emitter.send(
//                    SseEmitter.event()
//                        .id("SYSTEM")
//                        .name("UNREAD_MESSAGE_COUNT")
//                        .data(
//                            UnreadMessageCountDto.of(
//                                chatRoomId = chatRoomId,
//                                unreadCount = unreadCount,
//                                lastMessage = topChats.first().content
//                            )
//                        )
//                )
//            }
//        }
    }

    fun subscribe1(): Flux<ServerSentEvent<Any>>? {
        sinks[SecurityUtils.getCurrentUserId()] = Sinks.many().multicast().onBackpressureBuffer()

        val sink = sinks[SecurityUtils.getCurrentUserId()] ?: return null
        return sink.asFlux().doOnCancel {
            log.info { "subscribe - userId: ${SecurityUtils.getCurrentUserId()}, onCompletion" }
            sink.tryEmitComplete()
            sinks.remove(SecurityUtils.getCurrentUserId())
        }
    }
}
