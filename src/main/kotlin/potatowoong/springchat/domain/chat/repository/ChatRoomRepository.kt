package potatowoong.springchat.domain.chat.repository

import org.springframework.data.mongodb.repository.MongoRepository
import potatowoong.springchat.domain.chat.entity.ChatRoom

interface ChatRoomRepository : MongoRepository<ChatRoom, String>, ChatRoomRepositoryCustom {
}