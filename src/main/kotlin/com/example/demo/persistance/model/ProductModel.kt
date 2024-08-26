package com.example.demo.persistance.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("products")
data class ProductModel(
    @Id var id: Int?,
    val name: String,
    val price: Int,
    val isAvailable: Boolean?,
    val description: String?
)