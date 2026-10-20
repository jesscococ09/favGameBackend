package com.example.demo.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI
import java.util.NoSuchElementException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(
        exception: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        return problem(
            HttpStatus.BAD_REQUEST,
            exception.message ?: "Invalid request.",
            request.requestURI
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(
        exception: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        return problem(
            HttpStatus.BAD_REQUEST,
            "Invalid request body.",
            request.requestURI
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(
        exception: NoSuchElementException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        return problem(
            HttpStatus.NOT_FOUND,
            exception.message ?: "Resource not found.",
            request.requestURI
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleForbidden(
        exception: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        return problem(
            HttpStatus.FORBIDDEN,
            "Access denied.",
            request.requestURI
        )
    }

    private fun problem(
        status: HttpStatus,
        detail: String,
        path: String
    ): ResponseEntity<ProblemDetail> {
        val problem = ProblemDetail.forStatusAndDetail(status, detail)
        problem.title = status.reasonPhrase
        problem.instance = URI.create(path)
        return ResponseEntity.status(status).body(problem)
    }
}
