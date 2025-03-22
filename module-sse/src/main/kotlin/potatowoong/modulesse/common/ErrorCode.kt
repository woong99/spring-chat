package potatowoong.modulesse.common

import org.springframework.http.HttpStatus
import potatowoong.modulecommon.exception.CommonErrorCode

enum class ErrorCode(
    override val httpStatus: Int,
    override val errorCode: String,
    override val message: String,
) : CommonErrorCode {

    NOT_FOUND_FRIEND(HttpStatus.NOT_FOUND.value(), "FR001", "존재하지 않는 친구입니다."),
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND.value(), "CR001", "존재하지 않는 채팅방입니다.");

    override fun getErrorName(): String {
        return this.name
    }
}
