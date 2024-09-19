package com.love.timi.exception

import com.love.timi.response.RestResponse
import org.eclipse.angus.mail.util.MailConnectException
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailSendException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@RestControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

    /**
     * 정의해 둔 에러 발생시 응답 생성
     * @param exception 정의해 둔 에러 객체
     * @return Response
     */
    @ExceptionHandler(CustomException::class)
    protected fun customException(e: CustomException): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        e.printStackTrace()
        return RestResponse<HashMap<String, Any>>().customError(e).setBody(e.data).responseEntity()
    }

    /**
     * 정의하지 않은 에러 발생시 응답 생성
     * @param exception 발생한 에러 객체
     * @return Response (Unknown_error)
     */
    @ExceptionHandler(Exception::class)
    protected fun exception(e: Exception): ResponseEntity<RestResponse<HashMap<String, Any>>> {
        e.printStackTrace()
        val error = ErrorMessage.UNKNOWN_ERROR.exception
        return RestResponse<HashMap<String, Any>>().customError(error).setBody(error.data).responseEntity()
    }

    /**
     * 메일 전송 실패 에러 처리
     * @param e 메일 전송 실패 예외 객체
     * @return Response (메일 전송 실패 에러)
     */
    @ExceptionHandler(MailSendException::class)
    protected fun handleMailSendException(e: MailSendException): ResponseEntity<RestResponse<String?>> {
        println("메일 전송 실패 에러 처리")
        e.printStackTrace()

        // 원인이 MailConnectException인 경우
        val cause = e.cause
        return if (cause is MailConnectException) {
            // MailConnectException에 대한 처리
            RestResponse<String?>().customError(ErrorMessage.MAIL_SEND_CONNECTION_ERROR.exception).responseEntity()
        } else {
            // 다른 MailSendException에 대한 처리
            // 여기에서는 일반적인 메일 전송 실패로 처리할 수 있습니다.
            RestResponse<String?>().customError(ErrorMessage.MAIL_SEND_ERROR.exception).responseEntity()
        }
    }

}