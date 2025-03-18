package potatowoong.moduleapi.api.friend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.domainrdb.domains.auth.dto.SearchFriendDto
import potatowoong.domainrdb.domains.auth.repository.MemberRepository
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesecurity.utils.SecurityUtils

@Service
class FriendService(
    private val memberRepository: MemberRepository
) {

    @Transactional(readOnly = true)
    fun searchAllFriends(
        page: Long,
        searchQuery: String?,
    ): SearchFriendDto.Response {
        // 내 정보 조회
        val myInfo = memberRepository.findByIdOrNull(SecurityUtils.getCurrentUserId())
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        return memberRepository.searchAllFriends(page, searchQuery, myInfo.id!!)
    }
}