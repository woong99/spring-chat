package potatowoong.springchat.domain.chat.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.springchat.domain.auth.repository.MemberRepository
import potatowoong.springchat.domain.chat.dto.AllChatRoomsDto
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.domain.chat.dto.MyChatRoomsDto
import potatowoong.springchat.domain.chat.entity.ChatRoom
import potatowoong.springchat.domain.chat.entity.ChatRoomMember
import potatowoong.springchat.domain.chat.repository.ChatRoomMemberRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomRepository
import potatowoong.springchat.global.config.redis.RedisUtils
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode
import potatowoong.springchat.global.utils.SecurityUtils

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomMembersRepository: ChatRoomMemberRepository,
    private val redisUtils: RedisUtils
) {
    private val log = KotlinLogging.logger { }

    @Transactional
    fun addChatRoom(
        request: ChatRoomDto.Request
    ) {
        // 사용자 정보 조회
        val member = memberRepository.findByIdOrNull(SecurityUtils.getCurrentUserId())
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        // 채팅방 생성
        val chatRoom = chatRoomRepository.save(ChatRoom.of(request))
        chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom.id!!, member.id!!))
    }

    @Transactional(readOnly = true)
    fun getAllChatRooms(): List<AllChatRoomsDto> {
//        // 마지막 메시지 전송 일시 조회
//        val lastSendMessageMap = chatMessageRepository.findTop10LastSendAtMessage(0).associate {
//            it.chatRoomId to it.sendAt
//        }

        // 채팅방 목록 조회
//        val chatRooms = chatRoomRepository.findAllChatRooms()
//        chatRooms.forEach {
//            lastSendMessageMap[it.chatRoomId]?.let { sendAt ->
//                it.updateLastSendAt(sendAt)
//            }
//        }
        return listOf() // TODO : 수정
    }

    @Transactional(readOnly = true)
    fun getMyChatRooms(): List<MyChatRoomsDto> {
        // 내 채팅방 목록 조회
        val myChatRooms = chatRoomRepository.findMyChatRooms()
        val myChatRoomsMap = myChatRooms.associateBy { it.chatRoomId }

        // 현재 참여중인 채팅방인 경우 안읽은 메시지 수 0으로 변경
        redisUtils.getDataFromSet(SecurityUtils.getCurrentUserId().toString())?.let { chatRoomIds ->
            chatRoomIds.forEach { chatRoomId ->
                myChatRoomsMap[chatRoomId.toString().split("-")[0]]?.markAllMessageAsRead()
            }
        }

        return myChatRooms
    }

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
            savedChatRoomMember.updateLastJoinedAt()
            chatRoomMembersRepository.save(savedChatRoomMember)
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

        savedChatRoomMember.updateLastJoinedAt()
        chatRoomMembersRepository.save(savedChatRoomMember)

        // Redis에서 입장한 채팅방 ID 삭제
        redisUtils.deleteDataFromSet(memberId.toString(), "${chatRoomId}-${simpSessionId}")
    }
}
