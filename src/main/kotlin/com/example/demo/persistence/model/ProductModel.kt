package com.example.demo.persistence.model

import com.example.demo.controller.model.Product
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("products")
data class ProductModel(
    @Id val id: Int,
    val name: String,
    val price: Int,
    val isAvailable: Boolean?,
    val description: String?
) {
    constructor(product: Product) : this(
        id = product.id,
        name = product.name,
        price = product.price,
        isAvailable = product.isAvailable,
        description = product.description
    )
}