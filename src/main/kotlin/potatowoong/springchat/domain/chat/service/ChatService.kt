package potatowoong.springchat.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.springchat.domain.auth.repository.MemberRepository
import potatowoong.springchat.domain.chat.dto.ChatDto
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.entity.ChatMessage
import potatowoong.springchat.domain.chat.repository.ChatMessageRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomRepository
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode

@Service
class ChatService(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
) {
    @Transactional
    fun saveChat(
        chatRoomId: String,
        request: MessageDto.Request,
        memberId: Long
    ) {
        // 채팅방 정보 조회
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)

        // 채팅 저장
        chatMessageRepository.save(
            ChatMessage(
                content = request.message,
                memberId = memberId,
                chatRoomId = chatRoom.chatRoomId!!
            )
        )
    }

    @Transactional(readOnly = true)
    fun getChatList(
        chatRoomId: String,
        page: Long
    ): ChatDto.Response {
        // 채팅방 정보 조회
        val chatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)

        // 채팅 내역 조회
        val messages = chatMessageRepository.findMessagesWithPaging(chatRoomId, page)

        // 채팅방에 속한 멤버들의 닉네임 조회
        val memberIds = messages.map { it.memberId }.distinct()
        val nicknameMap = memberRepository.findByIdIn(memberIds)
            .associate { it.id!! to it.nickname }

        return ChatDto.Response.of(
            chatRoomName = chatRoom.name,
            nicknameMap = nicknameMap,
            messages = messages
        )
    }
}
