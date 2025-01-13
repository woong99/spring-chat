package potatowoong.springchat.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.springchat.domain.auth.repository.MemberRepository
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.entity.Chat
import potatowoong.springchat.domain.chat.repository.ChatRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomRepository
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository
) {
    @Transactional
    fun saveChat(
        chatRoomId: String,
        request: MessageDto.Request,
        memberId: Long
    ): String {
        // 사용자 정보 조회
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        // 채팅방 정보 조회
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)

        // 채팅 내역 저장
        chatRepository.save(
            Chat.of(
                request = request,
                member = member,
                chatRoom = chatRoom
            )
        )

        return member.nickname
    }

    fun getChatList(
        chatRoomId: String
    ): MessageDto.Response {
        // 채팅방 정보 조회
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)

        // 채팅 내역 조회
        val messages = chatRepository.findAllByChatRoomChatRoomIdOrderBySendAtDesc(chatRoomId)
        return MessageDto.Response.of(
            chatRoomName = chatRoom.name,
            messages = messages
        )
    }
}
