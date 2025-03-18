package potatowoong.moduleapi.api.auth.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import potatowoong.domainrdb.domains.auth.repository.MemberRepository
import potatowoong.moduleapi.api.auth.dto.LoginDto
import potatowoong.moduleapi.api.auth.dto.MemberDto
import potatowoong.moduleapi.api.auth.dto.SignupDto
import potatowoong.moduleapi.api.file.components.FileUtils
import potatowoong.moduleapi.api.file.enums.S3Folder
import potatowoong.moduleapi.common.exception.ErrorCode
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesecurity.auth.enums.Role
import potatowoong.modulesecurity.auth.jwt.components.JwtTokenProvider
import potatowoong.modulesecurity.auth.jwt.dto.TokenDto
import potatowoong.modulesecurity.utils.SecurityUtils

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val fileUtils: FileUtils
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
        val member = request.toEntity()
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
    ): MemberDto.Response {
        return MemberDto.Response.of(
            memberRepository.findByIdOrNull(memberId)
                ?: throw CustomException(ErrorCode.UNAUTHORIZED)
        )
    }

    @Transactional
    fun modifyMyInfo(
        request: MemberDto.UpdateRequest,
        profileImage: MultipartFile?
    ) {
        // 사용자 정보 조회
        val member = memberRepository.findByIdOrNull(SecurityUtils.getCurrentUserId())
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)

        // 닉네임 중복 체크
        checkSameNickname(request.nickname, member.id!!)

        // 이미지 업로드
        val s3Url = uploadProfileImage(profileImage)

        // 사용자 정보 수정
        member.modifyInfo(s3Url, request.nickname, request.introduction, request.defaultImageFlag)
    }

    /**
     * 닉네임 중복 체크
     *
     * @param nickname 변경하려는 닉네임
     * @param memberId 사용자 ID
     */
    private fun checkSameNickname(
        nickname: String,
        memberId: Long
    ) {
        val sameNicknameMember = memberRepository.findByNickname(nickname)
        if (sameNicknameMember != null && sameNicknameMember.id != memberId) {
            throw CustomException(ErrorCode.EXISTED_NICKNAME)
        }
    }

    /**
     * 프로필 이미지 업로드
     *
     * @param profileImage 프로필 이미지
     * @return S3 URL
     */
    private fun uploadProfileImage(
        profileImage: MultipartFile?
    ): String? {
        return profileImage?.takeIf { !it.isEmpty }
            ?.let { fileUtils.uploadImage(profileImage, S3Folder.PROFILE_IMAGE) }
    }
}
