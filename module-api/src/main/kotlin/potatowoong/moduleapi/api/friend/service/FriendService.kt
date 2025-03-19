package potatowoong.moduleapi.api.friend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.domainrdb.domains.auth.dto.SearchFriendDto
import potatowoong.domainrdb.domains.auth.entity.Friendship
import potatowoong.domainrdb.domains.auth.enums.FriendshipStatusFilter
import potatowoong.domainrdb.domains.auth.repository.FriendshipRepository
import potatowoong.domainrdb.domains.auth.repository.MemberRepository
import potatowoong.moduleapi.api.friend.dto.FriendDto
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesecurity.utils.SecurityUtils

@Service
class FriendService(
    private val memberRepository: MemberRepository,
    private val friendshipRepository: FriendshipRepository
) {

    @Transactional(readOnly = true)
    fun searchAllFriends(
        page: Long,
        searchQuery: String?,
        filter: FriendshipStatusFilter?
    ): SearchFriendDto.Response {
        // 내 정보 조회
        val myInfo = memberRepository.findByIdOrNull(SecurityUtils.getCurrentUserId())
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        return memberRepository.searchAllFriends(page, searchQuery, filter, myInfo.id!!)
    }

    @Transactional
    fun changeFriendStatus(
        request: FriendDto.ChangeFriendStatusRequest
    ) {
        // 친구 관계 정보 조회
        val friendship =
            friendshipRepository.findByMemberIdAndFriendId(SecurityUtils.getCurrentUserId(), request.friendId)

        if (friendship == null) {
            // 내 정보 조회
            val myInfo = memberRepository.findByIdOrNull(SecurityUtils.getCurrentUserId())
                ?: throw CustomException(ErrorCode.UNAUTHORIZED)

            // 친구 정보 조회
            val friendInfo = memberRepository.findByIdOrNull(request.friendId)
                ?: throw CustomException(ErrorCode.NOT_FOUND_FRIEND)

            val newFriendship = Friendship.of(
                member = myInfo,
                friend = friendInfo,
                friendshipStatus = request.status
            )
            friendshipRepository.save(newFriendship)
        } else {
            friendship.changeFriendshipStatus(request.status)
        }
    }
}