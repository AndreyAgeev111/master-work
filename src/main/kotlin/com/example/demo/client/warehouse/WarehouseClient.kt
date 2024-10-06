package com.example.demo.client.warehouse

import com.example.demo.client.warehouse.config.WarehouseConfiguration
import com.example.demo.client.warehouse.model.ProductUpdateRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

interface WarehouseClient {
    fun updateProduct(productId: Int, productUpdateRequest: ProductUpdateRequest)
}

@Component("warehouseClient")
class WarehouseClientImpl(
    private val warehouseConfiguration: WarehouseConfiguration,
    private val warehouseRestTemplate: RestTemplate
) : WarehouseClient {
    override fun updateProduct(productId: Int, productUpdateRequest: ProductUpdateRequest) {
        handleWith5xxError {
            warehouseRestTemplate.postForObject(
                UriComponentsBuilder
                    .fromHttpUrl(warehouseConfiguration.baseUrl)
                    .path(updateProductPath(productId))
                    .build()
                    .toUri(),
                productUpdateRequest,
                Unit::class.java
            )
        }
    }

    private fun handleWith5xxError(f: () -> Unit) {
        try {
            run(f)
        } catch (e: RestClientResponseException) {
            if (e.statusCode.is5xxServerError) {
                throw e
            } else {
                logger.error("received HTTP response with status ${e.statusCode}", e)
            }
        }
    }

    private fun updateProductPath(productId: Int) = "/api/v1/warehouse/$productId"

    private val logger = LoggerFactory.getLogger(this.javaClass)
}