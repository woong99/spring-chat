package potatowoong.domainrdb.domains.auth.repository

import potatowoong.domainrdb.domains.auth.dto.MemberWithFriendshipStatusDto
import potatowoong.domainrdb.domains.auth.dto.SearchFriendDto
import potatowoong.domainrdb.domains.auth.enums.FriendshipStatusFilter

interface MemberRepositoryCustom {

    fun searchAllFriends(
        page: Long,
        searchQuery: String?,
        filter: FriendshipStatusFilter?,
        userId: Long
    ): SearchFriendDto.Response

    fun findMemberWithFriendshipStatus(userIds: List<Long>): List<MemberWithFriendshipStatusDto>
}