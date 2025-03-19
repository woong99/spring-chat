package potatowoong.domainrdb.domains.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import potatowoong.domainrdb.domains.auth.entity.Friendship

interface FriendshipRepository : JpaRepository<Friendship, Long> {

    fun findByMemberIdAndFriendId(memberId: Long, friendId: Long): Friendship?
}