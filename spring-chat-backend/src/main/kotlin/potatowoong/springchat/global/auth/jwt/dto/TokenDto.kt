package potatowoong.springchat.global.auth.jwt.dto

import java.util.*

data class TokenDto(
    val token: String,
    val expiresIn: Long
) {
    companion object {
        fun of(
            token: String,
            expiresIn: Date
        ) = TokenDto(
            token = token,
            expiresIn = expiresIn.time
        )
    }
}
