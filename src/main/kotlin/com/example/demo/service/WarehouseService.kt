package com.example.demo.service

import com.example.demo.client.warehouse.WarehouseClient
import com.example.demo.client.warehouse.model.ProductUpdateRequest
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException

interface WarehouseService {
    fun updateProduct(productId: Int, updateProductRequest: ProductUpdateRequest)
}

@Service("warehouseService")
class WarehouseServiceImpl(val warehouseClient: WarehouseClient) : WarehouseService {

    @Retryable(maxAttempts = 5, backoff = Backoff(delay = 3000), retryFor = [RestClientResponseException::class])
    override fun updateProduct(productId: Int, updateProductRequest: ProductUpdateRequest) {
        warehouseClient.updateProduct(productId, updateProductRequest)
    }
}