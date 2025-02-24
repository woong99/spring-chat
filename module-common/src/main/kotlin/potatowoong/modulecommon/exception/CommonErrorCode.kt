package potatowoong.modulecommon.exception

interface CommonErrorCode {
    val httpStatus: Int
    val errorCode: String
    val message: String

    fun getErrorName(): String
}
