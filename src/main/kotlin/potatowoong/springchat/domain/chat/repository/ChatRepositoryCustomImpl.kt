package potatowoong.springchat.domain.chat.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import potatowoong.springchat.domain.chat.dto.ChatRoomDto
import potatowoong.springchat.domain.chat.entity.QChat.chat
import potatowoong.springchat.domain.chat.entity.QChatRoom.chatRoom

class ChatRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : ChatRepositoryCustom {

    override fun findChatRoomsWithLastChat(): List<ChatRoomDto.Response> {
        return queryFactory.select(
            Projections.constructor(
                ChatRoomDto.Response::class.java,
                chatRoom.chatRoomId,
                chatRoom.name,
                chat.content,
                chat.sendAt
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
            .fetch()
    }
}
