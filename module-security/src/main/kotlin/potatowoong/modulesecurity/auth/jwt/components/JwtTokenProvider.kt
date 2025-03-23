package potatowoong.modulesecurity.auth.jwt.components

import io.jsonwebtoken.*
import io.jsonwebtoken.Jwts.SIG
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import potatowoong.modulecommon.exception.CustomException
import potatowoong.modulesecurity.auth.data.CustomUserDetails
import potatowoong.modulesecurity.auth.jwt.dto.TokenDto
import potatowoong.modulesecurity.exception.ErrorCode
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val secretKey: String,
    @Value("\${jwt.token-expiration}")
    private val tokenExpiration: Long,
) {

    /**
     * Access Token, Refresh Token 생성
     */
    fun generateToken(
        authentication: Authentication,
        nickname: String,
        profileImageUrl: String?
    ): TokenDto {
        val authorities = authentication.authorities.joinToString(",") {
            it.authority
        }

        val now = Date().time

        // Access Token 만료 시간 설정
        val accessTokenExpiresIn = Date(now + 1000 * tokenExpiration)

        // Access Token 생성
        val accessToken = Jwts.builder()
            .subject(authentication.name)
            .claim("auth", authorities)
            .claim("nickname", nickname)
            .claim("profileImageUrl", profileImageUrl)
            .expiration(accessTokenExpiresIn)
            .signWith(getSigningKey(), SIG.HS256)
            .compact()

        return TokenDto.of(
            token = accessToken,
            expiresIn = accessTokenExpiresIn
        )
    }

    /**
     * Access Token을 파싱하여 Authentication 객체를 반환
     */
    fun getAuthentication(
        accessToken: String
    ): Authentication {
        // 토큰 복호화
        val claims = parseClaims(accessToken)
        if (claims["auth"] == null || claims["nickname"] == null) {
            throw CustomException(ErrorCode.INVALID_ACCESS_TOKEN)
        }

        // 권한정보 획득
        val authorities = claims["auth"].toString()
            .split(",")
            .map {
                SimpleGrantedAuthority(it)
            }

        val principal = CustomUserDetails(
            claims.subject.toLong(),
            claims["nickname"].toString(),
            claims["profileImageUrl"]?.toString(),
            authorities
        )
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    /**
     * AccessToken의 유효성 검증
     */
    fun validateToken(
        accessToken: String
    ): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(accessToken)
            true
        } catch (e: SecurityException) {
            throw CustomException(ErrorCode.INVALID_ACCESS_TOKEN)
        } catch (e: MalformedJwtException) {
            throw CustomException(ErrorCode.INVALID_ACCESS_TOKEN)
        } catch (e: UnsupportedJwtException) {
            throw CustomException(ErrorCode.INVALID_ACCESS_TOKEN)
        } catch (e: ExpiredJwtException) {
            throw CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN)
        }
    }

    /**
     * SecretKey를 생성하는 메소드
     */
    private fun getSigningKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(secretKey)

        return Keys.hmacShaKeyFor(keyBytes)!!
    }

    /**
     * AccessToken을 파싱하여 Claims를 반환하는 메소드
     */
    private fun parseClaims(
        accessToken: String
    ): Claims {
        return try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(accessToken)
                .payload
        } catch (e: ExpiredJwtException) {
            throw CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN)
        }
    }
}
