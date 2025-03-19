package potatowoong.domainrdb.domains.auth.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import potatowoong.domainrdb.domains.auth.dto.SearchFriendDto
import potatowoong.domainrdb.domains.auth.entity.QFriendship.friendship
import potatowoong.domainrdb.domains.auth.entity.QMember.member
import potatowoong.modulesecurity.utils.SecurityUtils

class MemberRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : MemberRepositoryCustom {

    override fun searchAllFriends(
        page: Long,
        searchQuery: String?,
        userId: Long
    ): SearchFriendDto.Response {
        val result = queryFactory.select(
            Projections.constructor(
                SearchFriendDto.Response.Friend::class.java,
                member.id,
                member.nickname,
                member.introduction,
                member.profileImageUrl,
                friendship.friendshipStatus
            )
        ).from(member)
            .leftJoin(friendship)
            .on(member.id.eq(friendship.friend.id).and(friendship.member.id.eq(SecurityUtils.getCurrentUserId())))
            .where(
                member.id.ne(userId)
                    .and(getSearchConditions(searchQuery))
            )
            .limit(21)
            .offset(page * 20)
            .orderBy(member.nickname.asc())
            .fetch()

        return SearchFriendDto.Response.of(
            friends = result,
            page = page
        )
    }

    private fun getSearchConditions(searchQuery: String?): BooleanBuilder {
        val builder = BooleanBuilder()

        // 닉네임 검색
        if (!searchQuery.isNullOrBlank()) {
            builder.and(member.nickname.contains(searchQuery))
        }

        return builder
    }
}