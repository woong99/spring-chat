package potatowoong.domainwebsocket.config.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import potatowoong.modulesecurity.auth.data.CustomUserDetails

data class StompCustomUserDetails(
    val stompSessionId: String,
    val id: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val authorities: List<SimpleGrantedAuthority>
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities.toMutableList()
    }

    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return this.stompSessionId
    }

    companion object {
        fun createStompAuthentication(
            authentication: Authentication,
            stompSessionId: String
        ): Authentication {
            val stompCustomUserDetails = createStompCustomUserDetails(authentication, stompSessionId)

            return UsernamePasswordAuthenticationToken(stompCustomUserDetails, "", stompCustomUserDetails.authorities)
        }

        private fun createStompCustomUserDetails(
            authentication: Authentication,
            stompSessionId: String
        ): StompCustomUserDetails {
            val principal = authentication.principal as CustomUserDetails
            return StompCustomUserDetails(
                stompSessionId = stompSessionId,
                id = principal.id,
                nickname = principal.nickname,
                profileImageUrl = principal.profileImageUrl,
                authorities = principal.authorities
            )
        }
    }
}