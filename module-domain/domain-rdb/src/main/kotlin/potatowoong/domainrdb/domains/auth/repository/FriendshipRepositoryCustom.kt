package potatowoong.domainrdb.domains.auth.repository

interface FriendshipRepositoryCustom {

    fun findMyFriendIds(friendsIds: List<Long>): List<Long>
}