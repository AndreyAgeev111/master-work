package com.example.demo.controller.model.response

import com.example.demo.controller.model.Product
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Model for answering the product search query")
data class GetProductResponse(
    val product: Product
)