package potatowoong.domainmongo.domains.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import potatowoong.domainmongo.domains.chat.entity.ChatRoomMember

interface ChatRoomMemberRepository : MongoRepository<ChatRoomMember, String>, ChatRoomMemberRepositoryCustom {

    fun findByChatRoomIdAndMemberId(chatRoomId: ObjectId, memberId: Long): ChatRoomMember?
}