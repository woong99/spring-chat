package potatowoong.springchat.domain.chat.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPQLQueryFactory
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.domain.chat.entity.QChat.chat
import potatowoong.springchat.domain.chat.entity.QChatRoom.chatRoom
import potatowoong.springchat.domain.chat.entity.QChatRoomMember.chatRoomMember

class ChatRoomRepositoryCustomImpl(
    private val queryFactory: JPQLQueryFactory
) : ChatRoomRepositoryCustom {

    override fun findChatRoomsWithLastChat(): List<ChatRoomDto.Response> {
        return queryFactory.select(
            Projections.constructor(
                ChatRoomDto.Response::class.java,
                chatRoom.chatRoomId,
                chatRoom.name,
                chat.content,
                chat.sendAt,
                chatRoomMember.count()
            )
        )
            .from(chatRoom)
            .leftJoin(chat).on(
                chat.chatRoom.chatRoomId.eq(chatRoom.chatRoomId).and(
                    chat.sendAt.eq(
                        JPAExpressions.select(chat.sendAt.max())
                            .from(chat)
                            .where(chat.chatRoom.chatRoomId.eq(chatRoom.chatRoomId))
                    )
                )
            )
            .leftJoin(chatRoomMember).on(chatRoomMember.chatRoom.chatRoomId.eq(chatRoom.chatRoomId))
            .groupBy(chatRoom.chatRoomId)
            .orderBy(chat.sendAt.desc())
            .fetch()
    }
}
