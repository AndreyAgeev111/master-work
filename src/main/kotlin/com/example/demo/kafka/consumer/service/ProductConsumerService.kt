package com.example.demo.kafka.consumer.service

import com.example.demo.client.warehouse.model.ProductUpdateRequest
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.service.WarehouseService
import org.springframework.stereotype.Service

interface ProductConsumerService {
    fun processEvent(productReserveEvent: ProductReserveEvent)
}

@Service("productConsumerService")
class ProductConsumerServiceImpl(private val warehouseService: WarehouseService) : ProductConsumerService {
    override fun processEvent(productReserveEvent: ProductReserveEvent) {
        val productUpdateRequest = ProductUpdateRequest(
            name = null,
            price = null,
            isAvailable = true,
            description = null,
            updateDate = productReserveEvent.date
        )
        warehouseService.updateProduct(productId = productReserveEvent.productId, productUpdateRequest)
    }
}