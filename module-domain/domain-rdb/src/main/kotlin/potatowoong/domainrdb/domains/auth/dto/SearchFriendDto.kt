package potatowoong.domainrdb.domains.auth.dto

import potatowoong.domainrdb.domains.auth.enums.FriendshipStatus

class SearchFriendDto {

    data class Response(
        val data: List<Friend>,
        val hasMore: Boolean,
        val page: Long
    ) {
        data class Friend(
            val id: Long,
            val nickname: String,
            val introduction: String?,
            val profileImageUrl: String?,
            val friendshipStatus: FriendshipStatus?
        )

        companion object {
            fun of(
                friends: List<Friend>,
                page: Long
            ) = Response(
                data = friends.take(20),
                hasMore = friends.size > 20,
                page = page
            )
        }
    }
}
