package potatowoong.springchat.global.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import potatowoong.springchat.global.common.ApiResponse

@RestControllerAdvice
class SpringChatAdvice {

    private val log = KotlinLogging.logger { }

    /**
     * Handle CustomException
     */
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiResponse<Unit>> {
        return ApiResponse.error(e.errorCode)
    }

    /**
     * Handle MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        return ApiResponse.fail(e.bindingResult.allErrors[0].defaultMessage)
    }

    /**
     * Handle Exception
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error(e) { "Exception" }
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}