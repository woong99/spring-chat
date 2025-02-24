package potatowoong.moduleapi.api.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.security.crypto.password.PasswordEncoder
import potatowoong.domainrdb.domains.auth.entity.Member

class SignupDto {

    data class Request(
        @field:NotBlank(message = "아이디는 필수 입력 값입니다.")
        @field:Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
        val userId: String,

        @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @field:Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해주세요.")
        var password: String,

        @field:NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @field:Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
        val nickname: String
    ) {
        // 비밀번호 암호화
        fun encryptPassword(
            passwordEncoder: PasswordEncoder
        ) {
            password = passwordEncoder.encode(password)
        }

        fun toEntity() = Member(
            userId = this.userId,
            password = this.password,
            nickname = this.nickname
        )
    }
}
