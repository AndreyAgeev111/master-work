package com.example.demo.controller.error

import com.example.demo.controller.model.response.error.ErrorResponse
import com.example.demo.service.exception.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun handleIllegalStateException(ex: ProductNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message), HttpStatus.UNPROCESSABLE_ENTITY)
    }
}