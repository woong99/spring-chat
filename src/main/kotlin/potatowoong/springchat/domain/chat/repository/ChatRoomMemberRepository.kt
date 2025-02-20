package potatowoong.springchat.domain.chat.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import potatowoong.springchat.domain.chat.entity.ChatRoomMember

interface ChatRoomMemberRepository : MongoRepository<ChatRoomMember, String> {

    fun findByChatRoomIdAndMemberId(chatRoomId: ObjectId, memberId: Long): ChatRoomMember?
}