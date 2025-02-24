package potatowoong.domainrdb.domains.auth.entity

import jakarta.persistence.*

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
}
