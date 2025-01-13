package potatowoong.springchat.domain.auth.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import potatowoong.springchat.domain.auth.dto.LoginDto
import potatowoong.springchat.domain.auth.dto.MemberDto
import potatowoong.springchat.domain.auth.dto.SignupDto
import potatowoong.springchat.domain.auth.service.AuthService
import potatowoong.springchat.global.auth.jwt.dto.TokenDto
import potatowoong.springchat.global.common.ApiResponse

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    /**
     * 회원가입 API
     */
    @PostMapping("/sign-up")
    fun signup(
        @Valid @RequestBody request: SignupDto.Request
    ): ResponseEntity<ApiResponse<Unit>> {
        authService.signup(request)

        return ApiResponse.success()
    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginDto.Request
    ): ResponseEntity<ApiResponse<TokenDto>> {
        return ApiResponse.success(authService.login(request))
    }

    /**
     * 내 정보 조회 API
     */
    @GetMapping("/me")
    fun me(): ResponseEntity<ApiResponse<MemberDto>> {
        return ApiResponse.success(authService.getMyInfo())
    }
}
