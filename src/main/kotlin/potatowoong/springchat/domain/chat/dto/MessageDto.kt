package potatowoong.springchat.domain.chat.dto

class MessageDto {
    data class Request(
        val message: String,
        val roomId: String
    )

    data class Response(
        val sender: String,
        val message: String,
        val createdAt: Long
    ) {
        companion object {
            fun of(
                sender: String,
                message: String,
            ) = Response(
                sender = sender,
                message = message,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}