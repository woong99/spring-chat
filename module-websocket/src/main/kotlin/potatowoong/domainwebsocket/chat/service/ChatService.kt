package potatowoong.domainwebsocket.chat.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.domainmongo.domains.chat.entity.Chat
import potatowoong.domainmongo.domains.chat.repository.ChatRepository
import potatowoong.domainmongo.domains.chat.repository.ChatRoomRepository
import potatowoong.domainwebsocket.chat.dto.MessageDto
import potatowoong.domainwebsocket.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException

@Service
class ChatService(
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
}
