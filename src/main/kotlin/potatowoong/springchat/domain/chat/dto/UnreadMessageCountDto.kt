package potatowoong.springchat.domain.chat.dto

data class UnreadMessageCountDto(
    val chatRoomId: String,
    val unreadCount: Int,
    val lastMessage: String
) {
    companion object {
        fun of(
            chatRoomId: String,
            unreadCount: Int,
            lastMessage: String
        ) = UnreadMessageCountDto(
            chatRoomId = chatRoomId,
            unreadCount = unreadCount,
            lastMessage = lastMessage
        )
    }
}