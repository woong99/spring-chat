package potatowoong.domainrdb.domains.auth.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import potatowoong.domainrdb.domains.auth.entity.QFriendship.friendship
import potatowoong.domainrdb.domains.auth.enums.FriendshipStatus
import potatowoong.modulesecurity.utils.SecurityUtils

class FriendshipRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : FriendshipRepositoryCustom {

    override fun findMyFriendIds(friendsIds: List<Long>): List<Long> {
        return queryFactory.select(friendship.friend.id)
            .from(friendship)
            .where(
                friendship.member.id.eq(SecurityUtils.getCurrentUserId())
                    .and(friendship.friend.id.`in`(friendsIds))
                    .and(friendship.friendshipStatus.eq(FriendshipStatus.FRIEND))
            )
            .fetch()
    }
}