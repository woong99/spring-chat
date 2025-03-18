package potatowoong.domainrdb.domains.auth.dto

import potatowoong.domainrdb.domains.auth.entity.Member

class SearchFriendDto {

    data class Response(
        val data: List<Friend>,
        val hasMore: Boolean,
        val page: Long
    ) {
        data class Friend(
            val id: Long,
            val nickname: String,
            val introduction: String?,
            val profileImageUrl: String?,
        ) {
            companion object {
                fun of(
                    member: Member
                ) = Friend(
                    id = member.id!!,
                    nickname = member.nickname,
                    introduction = member.introduction,
                    profileImageUrl = member.profileImageUrl
                )
            }
        }

        companion object {
            fun of(
                friends: List<Friend>,
                page: Long
            ) = Response(
                data = friends.take(20),
                hasMore = friends.size > 20,
                page = page
            )
        }
    }
}
