package potatowoong.moduleapi.api.chat.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.domainmongo.domains.chat.dto.ChatRoomIdDto
import potatowoong.domainmongo.domains.chat.entity.ChatRoom
import potatowoong.domainmongo.domains.chat.entity.ChatRoomMember
import potatowoong.domainmongo.domains.chat.enums.ChatRoomType
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
    fun getMyChatRooms(): List<ChatRoomDto.Response> {
        // 내 채팅방 목록 조회
        val myChatRooms = chatRoomRepository.findMyChatRooms(SecurityUtils.getCurrentUserId())

        // 친구 ID 목록 파싱
        val friendIds = myChatRooms
            .filter { it.chatRoomType == ChatRoomType.PRIVATE }
            .flatMap { it.memberIds }
            .filter { it != SecurityUtils.getCurrentUserId() }
            .distinct()
        
        // 친구 ID에 해당하는 친구 닉네임 조회
        val friendNicknameMap = memberRepository.findByIdIn(friendIds)
            .associateBy({ it.id!! }, { it.nickname })

        // 현재 참여중인 채팅방인 경우 안읽은 메시지 수 0으로 변경(다른 화면으로 채팅을 보고 있는 경우를 처리하기 위한 코드)
        val myChatRoomsMap = myChatRooms.associateBy { it.chatRoomId }
        redisUtils.getDataFromSet(SecurityUtils.getCurrentUserId().toString())?.let { chatRoomIds ->
            chatRoomIds.forEach { chatRoomId ->
                myChatRoomsMap[chatRoomId.toString().split("-")[0]]?.markAllMessageAsRead()
            }
        }

        return myChatRooms.map {
            ChatRoomDto.Response.of(it, friendNicknameMap)
        }
    }

    @Transactional
    fun getPrivateChatRoomId(
        friendId: Long
    ): ChatRoomIdDto {
        // 1대1 채팅방 있는지 조회
        val chatRoomIdDto = chatRoomRepository.findPrivateChatRoomId(friendId)

        return if (chatRoomIdDto == null) {
            // 친구 정보 조회
            val friendInfo = memberRepository.findByIdOrNull(friendId)
                ?: throw CustomException(ErrorCode.NOT_FOUND_FRIEND)

            // 1대1 채팅방 생성
            val chatRoom = ChatRoom.createPrivateRoom()
            val savedChatRoom = chatRoomRepository.save(chatRoom)

            // 채팅방 멤버 생성
            val myChatRoomMember = ChatRoomMember.of(savedChatRoom.id!!, SecurityUtils.getCurrentUserId())
            val friendChatRoomMember = ChatRoomMember.of(savedChatRoom.id!!, friendInfo.id!!)
            chatRoomMemberRepository.save(myChatRoomMember)
            chatRoomMemberRepository.save(friendChatRoomMember)

            ChatRoomIdDto.of(savedChatRoom)
        } else {
            chatRoomIdDto
        }
    }
}
