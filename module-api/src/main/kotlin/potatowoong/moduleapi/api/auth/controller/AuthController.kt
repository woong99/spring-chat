package potatowoong.moduleapi.api.auth.controller

import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import potatowoong.moduleapi.api.auth.dto.LoginDto
import potatowoong.moduleapi.api.auth.dto.MemberDto
import potatowoong.moduleapi.api.auth.dto.SignupDto
import potatowoong.moduleapi.api.auth.service.AuthService
import potatowoong.moduleapi.common.api.ApiResponse
import potatowoong.modulesecurity.auth.jwt.dto.TokenDto

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
    fun me(): ResponseEntity<ApiResponse<MemberDto.Response>> {
        return ApiResponse.success(authService.getMyInfo())
    }

    /**
     * 내 정보 수정 API
     */
    @PutMapping(value = ["/me"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateMyInfo(
        @Valid @RequestPart request: MemberDto.UpdateRequest,
        @RequestPart("profileImage") profileImage: MultipartFile?,
    ): ResponseEntity<ApiResponse<Unit>> {
        authService.modifyMyInfo(request, profileImage)
        
        return ApiResponse.success()
    }
}
