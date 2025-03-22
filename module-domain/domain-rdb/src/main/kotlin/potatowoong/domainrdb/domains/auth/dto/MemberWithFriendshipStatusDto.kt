package potatowoong.domainrdb.domains.auth.dto

import potatowoong.domainrdb.domains.auth.enums.FriendshipStatus

data class MemberWithFriendshipStatusDto(
    val id: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val friendshipStatus: FriendshipStatus?
) {
}