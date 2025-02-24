package potatowoong.modulesse.notification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import potatowoong.domainmongo.domains.chat.repository.ChatRepository
import potatowoong.domainmongo.domains.chat.repository.ChatRoomRepository
import potatowoong.modulesecurity.utils.SecurityUtils
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatRoomNotificationService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRepository: ChatRepository
) {

    private val log = KotlinLogging.logger { }

    private val emitters: MutableMap<Long, SseEmitter> = ConcurrentHashMap()

    fun subscribe(): SseEmitter {
        val emitter = SseEmitter(1000 * 60 * 10)

        val memberId = SecurityUtils.getCurrentUserId()
        emitters[memberId] = emitter

        emitter.onCompletion {
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
//        // 채팅방 및 채팅방 멤버 조회
//        val chatRoom = chatRoomRepository.findChatRoomAndChatRoomMembersByChatRoomId(chatRoomId)
//            ?: run {
//                log.error { "sendToClient - chatRoomId: $chatRoomId, error: ChatRoom not found" }
//                return
//            }
//
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
}
