package com.mvp.payment.application.v1.handler

import com.mvp.payment.domain.model.auth.ApiErrorResponse
import com.mvp.payment.domain.model.exception.Exceptions
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.function.Consumer

@ControllerAdvice
class ControllerExceptionHandler {
    @ExceptionHandler(Exceptions.NotFoundException::class)
    fun handleNotFoundException(e: Exceptions.NotFoundException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse(HttpStatus.NOT_FOUND.value(), e.message!!))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleRequestNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val errors: MutableList<String> = ArrayList()
        e.bindingResult
            .fieldErrors.forEach(Consumer { error: FieldError -> errors.add(error.field + ": " + error.defaultMessage) })
        e.bindingResult
            .globalErrors //Global errors are not associated with a specific field but are related to the entire object being validated.
            .forEach(Consumer { error: ObjectError -> errors.add(error.objectName + ": " + error.defaultMessage) })

        val message = "Validation of request failed: %s".formatted(java.lang.String.join(", ", errors))
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"))
    }

    @ExceptionHandler(Exceptions.DuplicateException::class)
    fun handleDuplicateException(e: Exceptions.DuplicateException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                e.message!!
            )
        )
    }

    @ExceptionHandler(InternalAuthenticationServiceException::class)
    fun handleInternalAuthenticationServiceException(e: InternalAuthenticationServiceException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                e.message!!
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(e: Exception): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name
            )
        )
    }
}