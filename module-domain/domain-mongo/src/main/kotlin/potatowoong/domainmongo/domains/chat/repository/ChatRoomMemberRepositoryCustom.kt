package potatowoong.domainmongo.domains.chat.repository

import org.bson.types.ObjectId
import potatowoong.domainmongo.domains.chat.entity.ChatRoomMember

interface ChatRoomMemberRepositoryCustom {

    fun updateLastJoinedAt(id: ObjectId)

    fun bulkInsertChatRoom(chatRoomMembers: List<ChatRoomMember>)
}