package potatowoong.springchat.global.exception

class CustomException(
    val errorCode: ErrorCode
) : RuntimeException()
