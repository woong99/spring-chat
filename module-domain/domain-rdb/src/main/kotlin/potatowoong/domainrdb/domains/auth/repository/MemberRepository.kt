package potatowoong.domainrdb.domains.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import potatowoong.domainrdb.domains.auth.entity.Member

interface MemberRepository : JpaRepository<Member, Long> {

    fun existsByUserId(userId: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun findByUserId(userId: String): Member?

    fun findByIdIn(ids: List<Long>): List<Member>
}
