package potatowoong.modulesse.notification.service

import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.codec.ServerSentEvent
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import potatowoong.domainmongo.domains.chat.entity.Chat
import potatowoong.domainmongo.domains.chat.entity.ChatRoomMember
import potatowoong.domainmongo.domains.chat.enums.ChatRoomType
import potatowoong.domainmongo.domains.chat.repository.ChatRepository
import potatowoong.domainmongo.domains.chat.repository.ChatRoomMemberRepository
import potatowoong.domainmongo.domains.chat.repository.ChatRoomRepository
import potatowoong.domainrdb.domains.auth.repository.FriendshipRepository
import potatowoong.domainredis.utils.RedisUtils
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesse.common.ErrorCode
import potatowoong.modulesse.notification.dto.NotificationDto
import potatowoong.modulesse.notification.dto.UnreadMessageCountDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatRoomNotificationService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRepository: ChatRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val friendshipRepository: FriendshipRepository,
    private val redisUtils: RedisUtils
) {
    private val sinks: MutableMap<Long, Sinks.Many<ServerSentEvent<Any>>> = ConcurrentHashMap()

    fun connect(): Flux<ServerSentEvent<Any>> {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication }
            .map { it.name }  // 현재 인증된 사용자 ID
            .flatMapMany { userId ->
                val sink = sinks.getOrPut(userId.toLong()) {
                    Sinks.many().multicast().onBackpressureBuffer()
                }

                sendToClient(
                    sink = sink,
                    event = CONNECT,
                    data = CONNECTED
                )

                sink.asFlux().doOnCancel {
                    finish(userId.toLong())
                }
            }
    }

    fun sendUnreadMessageCount(
        notificationDto: NotificationDto,
    ) {
        val chatRoomId = notificationDto.chatRoomId

        // 채팅방 정보 조회
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)

        // 채팅방 멤버 정보 조회
        val chatRoomMembers = chatRoomMemberRepository.findByChatRoomId(ObjectId(chatRoomId))

        // 차단된 친구인지 확인
        var blockedFriendId: Long? = null
        if (chatRoom.chatRoomType == ChatRoomType.PRIVATE) {
            val friendId = chatRoomMembers.first { it.memberId != notificationDto.sender }.memberId
            val friendship = friendshipRepository.findByMemberIdAndFriendId(friendId, notificationDto.sender)

            if (friendship != null && friendship.isBlocked()) {
                blockedFriendId = friendId
            }
        }

        // 채팅방 멤버별 Sinks 조회
        val sinks = sinks.filterKeys { chatRoomMembers.any { member -> member.memberId == it } }
        if (sinks.isEmpty()) {
            return
        }

        // 최근 100개 채팅 데이터 조회
        val chats = chatRepository.findTop100ByChatRoomIdOrderBySendAtDesc(ObjectId(chatRoomId))
        chatRoomMembers.forEach { chatRoomMember ->
            val sink = sinks[chatRoomMember.memberId] ?: return@forEach

            // 차단된 친구인 경우
            if (blockedFriendId != null) {
                return@forEach
            }

            sendToClient(
                sink = sink,
                event = UNREAD_MESSAGE_COUNT,
                data = UnreadMessageCountDto(
                    chatRoomId = chatRoomId,
                    unreadMessageCount = calculateUnreadMessageCount(chatRoomMember, chats, chatRoomId),
                    lastMessage = notificationDto.message,
                    lastSendAt = chats.first().sendAt
                )
            )
        }
    }

    /**
     * 안읽은 메시지 수를 계산하는 메소드
     *
     * @param chatRoomMember 채팅방 멤버 정보
     * @param chats 최근 100개 채팅 데이터
     * @param chatRoomId 채팅방 ID
     */
    private fun calculateUnreadMessageCount(
        chatRoomMember: ChatRoomMember,
        chats: List<Chat>,
        chatRoomId: String
    ): Int {
        // 현재 입장중인 채팅방이 있는지 Redis에서 조회
        val enteredChatRoomIds = redisUtils.getDataFromSet(chatRoomMember.memberId.toString())
            ?.map { it as String }

        val isEntered = enteredChatRoomIds?.any { it.startsWith(chatRoomId) } ?: false

        return when {
            chatRoomMember.lastJoinedAt == null -> chats.size
            isEntered -> 0
            else -> chats.filter { chat -> chat.sendAt > chatRoomMember.lastJoinedAt }.size
        }
    }

    /**
     * SSE 데이터 전송
     *
     * @param sink Sinks.Many
     * @param event 이벤트 이름
     * @param data 전송할 데이터
     */
    private fun sendToClient(
        sink: Sinks.Many<ServerSentEvent<Any>>,
        event: String,
        data: Any
    ) {
        sink.tryEmitNext(
            ServerSentEvent.builder<Any>()
                .id(SYSTEM)
                .event(event)
                .data(data)
                .build()
        )
    }

    /**
     * SSE 연결 종료
     *
     * @param userId 사용자 ID
     */
    private fun finish(
        userId: Long,
    ) {
        sinks[userId]?.tryEmitComplete()
        sinks.remove(userId)
    }

    companion object {
        private const val SYSTEM = "SYSTEM"
        private const val UNREAD_MESSAGE_COUNT = "UNREAD_MESSAGE_COUNT"
        private const val CONNECT = "CONNECT"
        private const val CONNECTED = "CONNECTED"
    }
}
