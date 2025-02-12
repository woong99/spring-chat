package potatowoong.springchat.domain.auth.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import potatowoong.springchat.domain.auth.dto.LoginDto
import potatowoong.springchat.domain.auth.dto.MemberDto
import potatowoong.springchat.domain.auth.dto.SignupDto
import potatowoong.springchat.domain.auth.entity.Member
import potatowoong.springchat.domain.auth.repository.MemberRepository
import potatowoong.springchat.global.auth.enums.Role
import potatowoong.springchat.global.auth.jwt.components.JwtTokenProvider
import potatowoong.springchat.global.auth.jwt.dto.TokenDto
import potatowoong.springchat.global.exception.CustomException
import potatowoong.springchat.global.exception.ErrorCode
import potatowoong.springchat.global.utils.SecurityUtils

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Transactional
    fun signup(
        request: SignupDto.Request
    ) {
        // 아이디 중복체크
        require(!memberRepository.existsByUserId(request.userId)) {
            throw CustomException(ErrorCode.EXISTED_USER_ID)
        }

        // 닉네임 중복체크
        require(!memberRepository.existsByNickname(request.nickname)) {
            throw CustomException(ErrorCode.EXISTED_NICKNAME)
        }

        // 비밀번호 암호화
        request.encryptPassword(passwordEncoder)

        // 회원 저장
        val member = Member.of(request)
        memberRepository.save(member)
    }

    @Transactional(readOnly = true)
    fun login(
        request: LoginDto.Request
    ): TokenDto {
        // ID로 회원 조회
        val savedMember = memberRepository.findByUserId(request.userId)
            ?: throw CustomException(ErrorCode.INCORRECT_ID_OR_PASSWORD)

        // Password 일치 여부 확인
        require(passwordEncoder.matches(request.password, savedMember.password)) {
            throw CustomException(ErrorCode.INCORRECT_ID_OR_PASSWORD)
        }

        // Authentication 객체 생성
        val authentication = UsernamePasswordAuthenticationToken(
            savedMember.id,
            savedMember.password,
            listOf(Role.ROLE_USER).map { SimpleGrantedAuthority(it.name) }
        )

        return jwtTokenProvider.generateToken(authentication, savedMember.nickname)
    }

    @Transactional(readOnly = true)
    fun getMyInfo(
        memberId: Long? = SecurityUtils.getCurrentUserId()
    ): MemberDto {
        return MemberDto.of(
            memberRepository.findByIdOrNull(memberId)
                ?: throw CustomException(ErrorCode.UNAUTHORIZED)
        )
    }
}
