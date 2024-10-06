package com.example.demo.controller.model.error

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Model for error response")
data class ErrorResponse(
    val description: String?
)