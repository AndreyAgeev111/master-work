package com.example.demo.kafka.consumer.service

import com.example.demo.client.warehouse.model.ProductUpdateRequest
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.service.WarehouseService
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import kotlin.test.Test

class ProductConsumerServiceTest {
    @Test
    fun whenConsumeProductReserveEvent_ShouldReturnNothing() {
        val productReserveEvent = ProductReserveEvent(
            productId = 1
        )
        val productUpdateRequest = ProductUpdateRequest(
            name = null,
            price = null,
            isAvailable = false,
            description = null,
            updateDate = productReserveEvent.date
        )


        val result = productConsumerService.processEvent(productReserveEvent)

        verify(warehouseService, atLeastOnce()).updateProduct(productReserveEvent.productId, productUpdateRequest)
        assertEquals(Unit, result)
    }

    private val warehouseService: WarehouseService = mock(WarehouseService::class.java)
    private val productConsumerService: ProductConsumerService = ProductConsumerServiceImpl(warehouseService)
}