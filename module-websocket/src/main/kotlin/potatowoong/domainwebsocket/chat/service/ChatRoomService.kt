package potatowoong.domainwebsocket.chat.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.domainmongo.domains.chat.entity.ChatRoomMember
import potatowoong.domainmongo.domains.chat.repository.ChatRoomMemberRepository
import potatowoong.domainredis.utils.RedisUtils
import potatowoong.domainwebsocket.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException

@Service
class ChatRoomService(
    private val chatRoomMembersRepository: ChatRoomMemberRepository,
    private val redisUtils: RedisUtils
) {
    private val log = KotlinLogging.logger { }

    @Transactional
    fun enterChatRoom(
        memberId: Long,
        chatRoomId: String,
        simpSessionId: String
    ) {
        // Redis에 입장한 채팅방 ID 저장
        redisUtils.setDataToSet(memberId.toString(), "${chatRoomId}-${simpSessionId}")

        // 채팅-사용자 관계 저장
        val savedChatRoomMember = chatRoomMembersRepository.findByChatRoomIdAndMemberId(ObjectId(chatRoomId), memberId)
        if (savedChatRoomMember == null) {
            val chatRoomMember = ChatRoomMember(
                chatRoomId = ObjectId(chatRoomId),
                memberId = memberId
            )
            chatRoomMembersRepository.save(chatRoomMember)
        } else {
            chatRoomMembersRepository.updateLastJoinedAt(savedChatRoomMember.id!!)
        }
    }

    @Transactional
    fun leaveChatRoom(
        memberId: Long,
        chatRoomId: String,
        simpSessionId: String
    ) {
        // 최근 입장 시간 갱신
        val savedChatRoomMember = chatRoomMembersRepository.findByChatRoomIdAndMemberId(ObjectId(chatRoomId), memberId)
            ?: throw CustomException(ErrorCode.NOT_FOUND_ENTER_CHAT_ROOM)
        chatRoomMembersRepository.updateLastJoinedAt(savedChatRoomMember.id!!)

        // Redis에서 입장한 채팅방 ID 삭제
        redisUtils.deleteDataFromSet(memberId.toString(), "${chatRoomId}-${simpSessionId}")
    }
}
