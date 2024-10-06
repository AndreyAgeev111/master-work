package com.example.demo.kafka.model

import java.time.Instant

data class ProductReserveEvent(
    val productId: Int,
    val date: Instant = Instant.now()
)
