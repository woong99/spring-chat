package potatowoong.domainmongo.domains.chat.repository

import org.springframework.data.mongodb.repository.MongoRepository
import potatowoong.domainmongo.domains.chat.entity.Chat

interface ChatRepository : MongoRepository<Chat, String>, ChatRepositoryCustom {

}
