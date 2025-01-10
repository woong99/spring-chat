package potatowoong.springchat.domain.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import potatowoong.springchat.domain.auth.entity.Member

interface MemberRepository : JpaRepository<Member, Long> {

    fun existsByUserId(userId: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun findByUserId(userId: String): Member?
}
