package potatowoong.springchat.domain.chat.dto

data class LastMessageDto(
    val chatRoomId: String,
    val unreadCount: Long
) {
}