package potatowoong.springchat.domain.chat.repository

import org.springframework.data.jpa.repository.JpaRepository
import potatowoong.springchat.domain.chat.entity.ChatRoomMember

interface ChatRoomMemberRepository : JpaRepository<ChatRoomMember, Long> {

    fun findByChatRoomChatRoomIdAndMemberId(chatRoomId: String, memberId: Long): ChatRoomMember?
}