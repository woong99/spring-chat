package potatowoong.moduleapi.api.auth.dto

import jakarta.validation.constraints.NotBlank
import potatowoong.domainrdb.domains.auth.entity.Member
import potatowoong.modulecommon.enums.UseFlag

class MemberDto {
    data class UpdateRequest(
        @field:NotBlank(message = "닉네임을 입력해주세요.")
        val nickname: String,
        val introduction: String?,
        val defaultImageFlag: UseFlag
    )

    data class Response(
        val id: Long,
        val userId: String,
        val nickname: String,
        val profileImageUrl: String?,
        val introduction: String?
    ) {
        companion object {
            fun of(
                member: Member
            ) = Response(
                id = member.id!!,
                userId = member.userId,
                nickname = member.nickname,
                profileImageUrl = member.profileImageUrl,
                introduction = member.introduction
            )
        }
    }
}
