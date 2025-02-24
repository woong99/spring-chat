package potatowoong.moduleapi.api.chat.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.domainmongo.domains.chat.dto.MyChatRoomsDto
import potatowoong.domainmongo.domains.chat.entity.ChatRoomMember
import potatowoong.domainmongo.domains.chat.repository.ChatRoomMemberRepository
import potatowoong.domainmongo.domains.chat.repository.ChatRoomRepository
import potatowoong.domainrdb.domains.auth.repository.MemberRepository
import potatowoong.domainredis.utils.RedisUtils
import potatowoong.moduleapi.api.chat.dto.AllChatRoomsDto
import potatowoong.moduleapi.api.chat.dto.ChatRoomDto
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesecurity.utils.SecurityUtils

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val memberRepository: MemberRepository,
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
        val chatRoom = chatRoomRepository.save(request.toEntity())
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
        val myChatRooms = chatRoomRepository.findMyChatRooms(SecurityUtils.getCurrentUserId())
        val myChatRoomsMap = myChatRooms.associateBy { it.chatRoomId }

        // 현재 참여중인 채팅방인 경우 안읽은 메시지 수 0으로 변경
        redisUtils.getDataFromSet(SecurityUtils.getCurrentUserId().toString())?.let { chatRoomIds ->
            chatRoomIds.forEach { chatRoomId ->
                myChatRoomsMap[chatRoomId.toString().split("-")[0]]?.markAllMessageAsRead()
            }
        }

        return myChatRooms
    }
}
