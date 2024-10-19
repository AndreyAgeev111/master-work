package com.example.demo.controller.model

import com.example.demo.persistence.model.ProductModel
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Model for product")
data class Product(
    @Schema(description = "ID of the product", example = "1")
    val id: Int,

    @Schema(description = "Name of the product", example = "Smartphone")
    val name: String,

    @Schema(description = "Price of the product in cents", example = "99999")
    val price: Int,

    @Schema(description = "Availability of the product", example = "true", nullable = true)
    val isAvailable: Boolean?,

    @Schema(
        description = "Optional description of the product",
        example = "Latest model with advanced features",
        nullable = true
    )
    val description: String?
) {
    constructor(model: ProductModel) : this(
        id = model.id,
        name = model.name,
        price = model.price,
        isAvailable = model.isAvailable,
        description = model.description
    )
}