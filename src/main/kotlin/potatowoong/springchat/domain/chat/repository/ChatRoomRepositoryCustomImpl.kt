package potatowoong.springchat.domain.chat.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPQLQueryFactory
import potatowoong.springchat.domain.chat.dto.AllChatRoomsDto
import potatowoong.springchat.domain.chat.dto.MyChatRoomsDto
import potatowoong.springchat.domain.chat.entity.QChatRoom.chatRoom
import potatowoong.springchat.domain.chat.entity.QChatRoomMember.chatRoomMember
import potatowoong.springchat.global.utils.SecurityUtils

class ChatRoomRepositoryCustomImpl(
    private val queryFactory: JPQLQueryFactory
) : ChatRoomRepositoryCustom {

    override fun findAllChatRooms(): List<AllChatRoomsDto> {
        return queryFactory.select(
            Projections.constructor(
                AllChatRoomsDto::class.java,
                chatRoom.chatRoomId,
                chatRoom.name,
                chatRoomMember.count(),
            )
        ).from(chatRoom)
            .leftJoin(chatRoomMember).on(chatRoomMember.chatRoom.chatRoomId.eq(chatRoom.chatRoomId))
            .groupBy(chatRoom.chatRoomId)
            .fetch()
    }

    override fun findMyChatRooms(): List<MyChatRoomsDto> {
        return queryFactory.select(
            Projections.constructor(
                MyChatRoomsDto::class.java,
                chatRoom.chatRoomId,
                chatRoom.name,
                chatRoomMember.lastJoinedAt,
                JPAExpressions.select(chatRoomMember.member.count())
                    .from(chatRoomMember)
                    .where(chatRoomMember.chatRoom.chatRoomId.eq(chatRoom.chatRoomId)),
            )
        )
            .from(chatRoom)
            .innerJoin(chatRoomMember).on(
                chatRoomMember.chatRoom.chatRoomId.eq(chatRoom.chatRoomId)
                    .and(chatRoomMember.member.id.eq(SecurityUtils.getCurrentUserId()))
            )
            .fetch()
    }
}
