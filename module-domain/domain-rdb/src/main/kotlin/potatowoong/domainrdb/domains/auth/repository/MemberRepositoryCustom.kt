package potatowoong.domainrdb.domains.auth.repository

import potatowoong.domainrdb.domains.auth.dto.SearchFriendDto

interface MemberRepositoryCustom {

    fun searchAllFriends(page: Long, searchQuery: String?, userId: Long): SearchFriendDto.Response
}