package potatowoong.domainrdb.domains.auth.entity

import jakarta.persistence.*
import potatowoong.modulecommon.enums.UseFlag

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
    var nickname: String,

    @Column(length = 255)
    var profileImageUrl: String? = null,

    @Column(length = 30)
    var introduction: String? = null
) {
    fun modifyInfo(
        profileImageUrl: String?,
        nickname: String,
        introduction: String?,
        defaultImageFlag: UseFlag,
    ) {
        if (defaultImageFlag == UseFlag.Y) {
            this.profileImageUrl = null
        } else {
            profileImageUrl?.let { this.profileImageUrl = it }
        }
        this.nickname = nickname
        this.introduction = introduction
    }
}
