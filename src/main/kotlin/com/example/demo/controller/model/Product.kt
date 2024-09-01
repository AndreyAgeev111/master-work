package com.example.demo.controller.model

import com.example.demo.persistance.model.ProductModel
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Model for product")
data class Product(
    val id: Int?,
    val name: String,
    val price: Int,
    val isAvailable: Boolean?,
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