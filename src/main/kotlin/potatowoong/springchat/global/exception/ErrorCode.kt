package potatowoong.springchat.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val errorCode: String,
    val message: String,
) {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ISE", "서버 내부 오류입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FBD", "권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNA", "인증되지 않았습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "IP", "잘못된 요청입니다."),

    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT001", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT002", "유효하지 않은 Access Token입니다."),

    EXISTED_USER_ID(HttpStatus.BAD_REQUEST, "AU001", "이미 존재하는 아이디입니다."),
    EXISTED_NICKNAME(HttpStatus.BAD_REQUEST, "AU002", "이미 존재하는 닉네임입니다."),
    INCORRECT_ID_OR_PASSWORD(HttpStatus.BAD_REQUEST, "AU003", "아이디 또는 비밀번호가 일치하지 않습니다."),
}
