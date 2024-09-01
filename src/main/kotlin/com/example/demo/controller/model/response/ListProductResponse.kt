package com.example.demo.controller.model.response

import com.example.demo.controller.model.Product
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Model for responding to a product list request")
data class ListProductResponse(
    val products: List<Product>
)