package com.example.demo.persistence.repository

import com.example.demo.persistence.model.ProductModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.util.ResourceUtils
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@TestPropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application-test.properties")
class ProductRepositoryTest {
    @Autowired
    lateinit var productRepository: ProductRepository

    @Test
    fun whenFindAllProducts_returnsListOfProductsInDatabase() {
        val expectedProduct = ProductModel(
            id = 1,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        productRepository.insert(expectedProduct)
        val product = productRepository.findAll().find { it.id == 1 }
        productRepository.deleteAll()

        if (product != null) {
            assertEquals(expectedProduct.name, product.name)
        }
    }

    @Test
    fun whenFindProductById_returnsProductById() {
        val expectedProduct = ProductModel(
            id = 1,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        productRepository.insert(expectedProduct)
        val expectedProductId = productRepository.findAll().iterator().next().id
        val product = productRepository.findById(expectedProductId ).get()
        productRepository.deleteAll()

        assertEquals(expectedProduct.name, product.name)
    }

    @Test
    fun whenSaveProduct_thenProductIsSaved() {
        val firstExpectedProduct = ProductModel(
            id = 1,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        productRepository.insert(firstExpectedProduct)
        val expectedProductId = productRepository.findAll().iterator().next().id

        val secondExpectedProduct = ProductModel(
            id = expectedProductId,
            name = UUID.randomUUID().toString(),
            price = 1000,
            isAvailable = true,
            description = null
        )
        val product = productRepository.save(secondExpectedProduct)
        productRepository.deleteAll()

        assertEquals(secondExpectedProduct.price, product.price)
        assertEquals(secondExpectedProduct.name, product.name)
    }
}