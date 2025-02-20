package potatowoong.springchat.domain.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.springchat.domain.auth.repository.MemberRepository
import potatowoong.springchat.domain.chat.dto.ChatDto
import potatowoong.springchat.domain.chat.dto.MessageDto
import potatowoong.springchat.domain.chat.entity.Chat
import potatowoong.springchat.domain.chat.repository.ChatRepository
import potatowoong.springchat.domain.chat.repository.ChatRoomRepository
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode

@Service
class ChatService(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRepository: ChatRepository,
) {
    @Transactional
    fun saveChat(
        chatRoomId: String,
        request: MessageDto.Request,
        memberId: Long
    ) {
        // 채팅방 정보 조회
        val mongoChatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)

        // 채팅 저장
        chatRepository.save(
            Chat(
                content = request.message,
                memberId = memberId,
                chatRoomId = mongoChatRoom.id!!,
            )
        )
    }

    @Transactional(readOnly = true)
    fun getChatList(
        chatRoomId: String,
        page: Long
    ): ChatDto.Response {
        // 채팅방 정보 조회
        val mongoChatRoom = chatRoomRepository.findByIdOrNull(chatRoomId)
            ?: throw CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)

        // 채팅 내역 조회
        val messages = chatRepository.findMessagesWithPaging(chatRoomId, page)

        // 채팅방에 속한 멤버들의 닉네임 조회
        val memberIds = messages.map { it.memberId }.distinct()
        val nicknameMap = memberRepository.findByIdIn(memberIds)
            .associate { it.id!! to it.nickname }

        return ChatDto.Response.of(
            chatRoomName = mongoChatRoom.name,
            nicknameMap = nicknameMap,
            messages = messages
        )
    }
}
