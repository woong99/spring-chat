package potatowoong.springchat.domain.chat.dto

data class MessageDto(
    val sender: String,
    val message: String,
    val createdAt: Long
) {
    companion object {
        fun of(
            sender: String,
            message: String
        ) = MessageDto(
            sender = sender,
            message = message,
            createdAt = System.currentTimeMillis()
        )
    }
}