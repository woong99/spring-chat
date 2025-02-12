package potatowoong.springchat.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.springchat.domain.auth.repository.MemberRepository
import potatowoong.springchat.domain.chat.dto.AllChatRoomsDto
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.domain.chat.dto.MyChatRoomsDto
import potatowoong.springchat.domain.chat.entity.ChatRoom
import potatowoong.springchat.domain.chat.entity.ChatRoomMember
import potatowoong.springchat.domain.chat.repository.ChatMessageRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomMemberRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomRepository
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode
import potatowoong.springchat.global.utils.SecurityUtils

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun addChatRoom(
        request: ChatRoomDto.Request
    ) {
        // 사용자 정보 조회
        val member = memberRepository.findByIdOrNull(SecurityUtils.getCurrentUserId())
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        // 채팅방 생성
        val chatRoom = chatRoomRepository.save(ChatRoom.of(request))
        chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom, member))
    }

    @Transactional(readOnly = true)
    fun getAllChatRooms(): List<AllChatRoomsDto> {
//        // 마지막 메시지 전송 일시 조회
//        val lastSendMessageMap = chatMessageRepository.findTop10LastSendAtMessage(0).associate {
//            it.chatRoomId to it.sendAt
//        }

        // 채팅방 목록 조회
        val chatRooms = chatRoomRepository.findAllChatRooms()
//        chatRooms.forEach {
//            lastSendMessageMap[it.chatRoomId]?.let { sendAt ->
//                it.updateLastSendAt(sendAt)
//            }
//        }
        return chatRooms
    }

    @Transactional(readOnly = true)
    fun getMyChatRooms(): List<MyChatRoomsDto> {
        // 내 채팅방 목록 조회
        val chatRooms = chatRoomRepository.findMyChatRooms()
        val chatRoomIds = chatRooms.map { it.chatRoomId }

        // 마지막 메시지 전송 일시 조회
        val lastSendMessageMap = chatMessageRepository.findLastMessages(chatRoomIds).associateBy { it.chatRoomId }

        // 마지막 메시지 추가
        chatRooms.forEach {
            lastSendMessageMap[it.chatRoomId]?.let { message ->
                it.updateLastChatMessage(message)
            }
        }

        // 안 읽은 메시지 수 조회
        val unreadCountMap = chatMessageRepository.findUnreadMessageCount(chatRooms).associate {
            it.chatRoomId to it.unreadCount
        }

        // 안 읽은 메시지 수 추가
        chatRooms.forEach {
            unreadCountMap[it.chatRoomId]?.let { unreadCount ->
                it.updateUnreadCount(unreadCount)
            }
        }

        // 정렬 (안 읽은 메시지가 많은 순, 마지막 메시지 전송 일시가 늦은 순)
        val (unreadRooms, readRooms) = chatRooms.partition { it.unreadCount > 0 }
        return unreadRooms.sortedByDescending { it.lastSendAt } + readRooms.sortedByDescending { it.lastSendAt }
    }

    @Transactional
    fun enterChatRoom(
        memberId: Long,
        chatRoomId: String
    ) {
        val savedChatRoomMember = chatRoomMemberRepository.findByChatRoomChatRoomIdAndMemberId(chatRoomId, memberId)
        if (savedChatRoomMember == null) {
            // 사용자 정보 조회
            val member = memberRepository.findByIdOrNull(memberId)
                ?: throw CustomException(ErrorCode.UNAUTHORIZED)

            // 채팅방 정보 조회
            val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
                ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)

            val chatRoomMember = ChatRoomMember.of(
                chatRoom = chatRoom,
                member = member
            )
            chatRoomMemberRepository.save(chatRoomMember)
        } else {
            savedChatRoomMember.updateLastJoinedAt()
        }
    }
}
