package com.example.demo.controller.error

import com.example.demo.controller.model.error.ErrorResponse
import com.example.demo.service.exception.EventNotFoundException
import com.example.demo.service.exception.EventProcessingFailedException
import com.example.demo.service.exception.ProductAlreadyReservedException
import com.example.demo.service.exception.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun handleProductNotFoundException(ex: ProductNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message), HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler
    fun handleEventNotFoundException(ex: EventNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message), HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler
    fun handleEventProcessingFailedException(ex: EventProcessingFailedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message), HttpStatus.EXPECTATION_FAILED)
    }

    @ExceptionHandler
    fun handleProductAlreadyReservedException(ex: ProductAlreadyReservedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message), HttpStatus.BAD_REQUEST)
    }
}