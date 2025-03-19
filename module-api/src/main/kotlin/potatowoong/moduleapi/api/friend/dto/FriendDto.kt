package potatowoong.moduleapi.api.friend.dto

import potatowoong.domainrdb.domains.auth.enums.FriendshipStatus

class FriendDto {

    data class ChangeFriendStatusRequest(
        val friendId: Long,
        val status: FriendshipStatus
    ) {
    }
}