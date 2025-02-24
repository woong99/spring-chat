package potatowoong.modulesecurity.exception

import org.springframework.http.HttpStatus
import potatowoong.modulecommon.exception.CommonErrorCode

enum class ErrorCode(
    override val httpStatus: Int,
    override val errorCode: String,
    override val message: String,
) : CommonErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ISE", "서버 내부 오류입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "FBD", "권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "UNA", "인증되지 않았습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST.value(), "IP", "잘못된 요청입니다."),

    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), "JWT001", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), "JWT002", "유효하지 않은 Access Token입니다."),

    EXISTED_USER_ID(HttpStatus.BAD_REQUEST.value(), "AU001", "이미 존재하는 아이디입니다."),
    EXISTED_NICKNAME(HttpStatus.BAD_REQUEST.value(), "AU002", "이미 존재하는 닉네임입니다."),
    INCORRECT_ID_OR_PASSWORD(HttpStatus.BAD_REQUEST.value(), "AU003", "아이디 또는 비밀번호가 일치하지 않습니다."),

    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND.value(), "CR001", "존재하지 않는 채팅방입니다."),
    NOT_FOUND_ENTER_CHAT_ROOM(HttpStatus.NOT_FOUND.value(), "CR002", "입장한 채팅방이 존재하지 않습니다."), ;

    override fun getErrorName(): String {
        return this.name
    }
}
