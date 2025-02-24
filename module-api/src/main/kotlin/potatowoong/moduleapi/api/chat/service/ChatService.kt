package potatowoong.moduleapi.api.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.domainmongo.domains.chat.repository.ChatRepository
import potatowoong.domainmongo.domains.chat.repository.ChatRoomRepository
import potatowoong.domainrdb.domains.auth.repository.MemberRepository
import potatowoong.moduleapi.api.chat.dto.ChatDto
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException

@Service
class ChatService(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRepository: ChatRepository,
) {
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
