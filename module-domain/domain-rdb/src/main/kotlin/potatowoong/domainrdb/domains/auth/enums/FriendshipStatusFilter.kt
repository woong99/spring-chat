package potatowoong.domainrdb.domains.auth.enums

enum class FriendshipStatusFilter {
    ALL,
    FRIEND,
    BLOCKED;

    fun getFriendshipStatus(): FriendshipStatus {
        return when (this) {
            FRIEND -> FriendshipStatus.FRIEND
            BLOCKED -> FriendshipStatus.BLOCKED
            else -> throw IllegalArgumentException("FriendshipStatusFilter.ALL is not allowed")
        }
    }
}