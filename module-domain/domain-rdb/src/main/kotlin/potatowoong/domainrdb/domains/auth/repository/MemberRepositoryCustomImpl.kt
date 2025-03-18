package potatowoong.domainrdb.domains.auth.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import potatowoong.domainrdb.domains.auth.dto.SearchFriendDto
import potatowoong.domainrdb.domains.auth.entity.QMember.member

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
                member.profileImageUrl
            )
        ).from(member)
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