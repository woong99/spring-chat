package potatowoong.moduleapi.api.auth.dto

import potatowoong.domainrdb.domains.auth.entity.Member

data class MemberDto(
    val id: Long,
    val userId: String,
    val nickname: String
) {
    companion object {
        fun of(
            member: Member
        ) = MemberDto(
            id = member.id!!,
            userId = member.userId,
            nickname = member.nickname
        )
    }
}
