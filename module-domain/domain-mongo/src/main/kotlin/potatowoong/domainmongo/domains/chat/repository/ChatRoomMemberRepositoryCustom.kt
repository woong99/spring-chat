package potatowoong.domainmongo.domains.chat.repository

import org.bson.types.ObjectId

interface ChatRoomMemberRepositoryCustom {

    fun updateLastJoinedAt(id: ObjectId)
}