package potatowoong.springchat.domain.chat.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.domain.chat.entity.ChatRoom
import potatowoong.springchat.domain.chat.repository.ChatRoomRepository

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository
) {
    @Transactional
    fun addChatRoom(
        request: ChatRoomDto.Request
    ) {
        chatRoomRepository.save(ChatRoom.of(request))
    }

    @Transactional(readOnly = true)
    fun getChatRooms(): List<ChatRoomDto.Response> {
        return chatRoomRepository.findAll().map {
            ChatRoomDto.Response.of(it)
        }
    }
}