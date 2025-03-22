package potatowoong.domainrdb.domains.auth.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import potatowoong.domainrdb.domains.auth.enums.FriendshipStatus
import potatowoong.domainrdb.domains.common.BaseEntity

@Entity
@Table(
    indexes = [
        Index(name = "idx_member_id", columnList = "member_id"),
        Index(name = "idx_friend_id", columnList = "friend_id"),
    ]
)
@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update friendship set deleted_at = now() where id = ?")
@Comment("친구 관계 정보")
class Friendship(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("회원 정보 ID")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("친구 정보 ID")
    val friend: Member,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(10)")
    var friendshipStatus: FriendshipStatus
) : BaseEntity() {

    fun changeFriendshipStatus(friendshipStatus: FriendshipStatus) {
        this.friendshipStatus = friendshipStatus
    }

    fun isBlocked(): Boolean {
        return friendshipStatus == FriendshipStatus.BLOCKED
    }

    companion object {
        fun of(
            member: Member,
            friend: Member,
            friendshipStatus: FriendshipStatus
        ) = Friendship(
            member = member,
            friend = friend,
            friendshipStatus = friendshipStatus
        )
    }
}