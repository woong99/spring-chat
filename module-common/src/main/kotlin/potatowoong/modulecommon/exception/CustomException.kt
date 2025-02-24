package potatowoong.modulecommon.exception

class CustomException(
    val errorCode: CommonErrorCode
) : RuntimeException()
