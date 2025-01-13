package potatowoong.springchat.domain.auth.entity

import jakarta.persistence.*
import potatowoong.springchat.domain.auth.dto.SignupDto

@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false, length = 20)
    val userId: String,

    @Column(nullable = false, length = 100)
    val password: String,

    @Column(nullable = false, length = 20)
    val nickname: String,
) {
    companion object {
        fun of(
            request: SignupDto.Request
        ) = Member(
            userId = request.userId,
            password = request.password,
            nickname = request.nickname
        )
    }
}
