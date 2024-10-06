package com.example.demo.service

import com.example.demo.client.warehouse.WarehouseClient
import com.example.demo.client.warehouse.model.ProductUpdateRequest
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.*
import java.time.Instant
import kotlin.test.Test

class WarehouseServiceTest {
    @Test
    fun whenUpdateProductInWarehouse_ReturnNothing() {
        val productId = 1
        val date = Instant.now()
        val productUpdateRequest = ProductUpdateRequest(
            name = null,
            price = null,
            isAvailable = true,
            description = null,
            updateDate = date
        )

        val result = warehouseService.updateProduct(productId, productUpdateRequest)

        verify(warehouseClient, atLeastOnce()).updateProduct(productId, productUpdateRequest)
        Assertions.assertEquals(result, Unit)
    }

    private val warehouseClient: WarehouseClient = mock(WarehouseClient::class.java)
    private val warehouseService: WarehouseService = WarehouseServiceImpl(warehouseClient)
}