package com.example.demo.client.warehouse

import com.example.demo.client.warehouse.config.WarehouseConfiguration
import com.example.demo.client.warehouse.model.ProductUpdateRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant

@TestConfiguration
class WarehouseTestConfiguration {
    @Bean
    fun warehouseConfiguration(): WarehouseConfiguration {
        val configuration = WarehouseConfiguration()
        configuration.baseUrl = "http://localhost:8443"
        return configuration
    }
}

@SpringBootTest
@Import(WarehouseTestConfiguration::class)
@TestPropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application-test.properties")
class WarehouseClientTest {
    @MockBean
    lateinit var warehouseRestTemplateConfiguration: RestTemplate

    @Autowired
    lateinit var warehouseClient: WarehouseClient

    @Test
    fun whenUpdateProductInWarehouse_ReturnNothing() {
        val productId = 1
        val date = Instant.now()
        val productUpdateRequest = ProductUpdateRequest(
            name = null,
            price = null,
            isAvailable = false,
            description = null,
            updateDate = date
        )
        val path = "/api/v1/warehouse/$productId"
        `when`(
            warehouseRestTemplateConfiguration.postForObject(
                UriComponentsBuilder
                    .fromHttpUrl(baseUrl)
                    .path(path)
                    .build()
                    .toUri(),
                productUpdateRequest,
                Unit::class.java
            )
        ).thenReturn(Unit)

        val result = warehouseClient.updateProduct(productId, productUpdateRequest)
        assertEquals(Unit, result)
    }

    @Test
    fun whenUpdateProductInWarehouseWithNot500Exception_thenReturnNothing() {
        val productId = 1
        val date = Instant.now()
        val productUpdateRequest = ProductUpdateRequest(
            name = null,
            price = null,
            isAvailable = false,
            description = null,
            updateDate = date
        )
        val path = "/api/v1/warehouse/$productId"
        `when`(
            warehouseRestTemplateConfiguration.postForObject(
                UriComponentsBuilder
                    .fromHttpUrl(baseUrl)
                    .path(path)
                    .build()
                    .toUri(),
                productUpdateRequest,
                Unit::class.java
            )
        ).thenThrow(RestClientResponseException("Some error", 404, "Not Found", null, null, null))

        val result = warehouseClient.updateProduct(productId, productUpdateRequest)
        assertEquals(Unit, result)
    }

    @Test
    fun whenUpdateProductInWarehouseWith500Exception_thenThrowException() {
        val productId = 1
        val date = Instant.now()
        val productUpdateRequest = ProductUpdateRequest(
            name = null,
            price = null,
            isAvailable = false,
            description = null,
            updateDate = date
        )
        val path = "/api/v1/warehouse/$productId"
        `when`(
            warehouseRestTemplateConfiguration.postForObject(
                UriComponentsBuilder
                    .fromHttpUrl(baseUrl)
                    .path(path)
                    .build()
                    .toUri(),
                productUpdateRequest,
                Unit::class.java
            )
        ).thenThrow(RestClientResponseException("Some error", 500, "Internal Server Error", null, null, null))

        assertThrows(RestClientResponseException::class.java) {
            warehouseClient.updateProduct(productId, productUpdateRequest)
        }
    }

    private val baseUrl = "http://localhost:8443"
}
