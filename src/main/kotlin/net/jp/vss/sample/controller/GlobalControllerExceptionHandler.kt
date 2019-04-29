package net.jp.vss.sample.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

/**
 * 全体で使用する Handler.
 *
 */
@ControllerAdvice
class GlobalControllerExceptionHandler {
    /**
     * ConstraintViolationException を throw した時は 400.
     *
     * @param response HttpServletResponse
     * @param ex 対象 Exception
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(response: HttpServletResponse, ex: Exception) {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Constraint Violation Exception")
    }
}
