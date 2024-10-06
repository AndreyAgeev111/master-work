package com.example.demo.client.warehouse.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductUpdateRequest(
    val name: String?,
    val price: Int?,
    val isAvailable: Boolean?,
    val description: String?,
    val updateDate: Instant
)
