package potatowoong.modulesecurity.auth.data

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomUserDetails(
    val id: Long,
    val nickname: String,
    val authorities: List<SimpleGrantedAuthority>
) : UserDetails {
    var stompSessionId: String? = null
        private set

    fun initStompSessionId(sessionId: String) {
        stompSessionId = sessionId
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities.toMutableList()
    }

    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return id.toString()
    }
}