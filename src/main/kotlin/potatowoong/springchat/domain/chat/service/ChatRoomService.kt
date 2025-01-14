package potatowoong.springchat.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.springchat.domain.auth.repository.MemberRepository
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.domain.chat.entity.ChatRoom
import potatowoong.springchat.domain.chat.entity.ChatRoomMember
import potatowoong.springchat.domain.chat.repository.ChatRoomMemberRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomRepository
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val memberRepository: MemberRepository
) {
    @Transactional
    fun addChatRoom(
        request: ChatRoomDto.Request
    ) {
        chatRoomRepository.save(ChatRoom.of(request))
    }

    @Transactional(readOnly = true)
    fun getChatRooms(): List<ChatRoomDto.Response> {
        return chatRoomRepository.findChatRoomsWithLastChat()
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
