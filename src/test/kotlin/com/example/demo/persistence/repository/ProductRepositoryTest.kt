package com.example.demo.persistence.repository

import com.example.demo.persistence.model.ProductModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
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
            id = null,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        productRepository.save(expectedProduct)
        val product = productRepository.findAll().find { it.id == 1 }
        productRepository.deleteAll()

        if (product != null) {
            Assertions.assertEquals(product.name, expectedProduct.name)
        }
    }

    @Test
    fun whenFindProductById_returnsProductById() {
        val expectedProduct = ProductModel(
            id = null,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        productRepository.save(expectedProduct)
        val expectedProductId = productRepository.findAll().iterator().next().id
        val product = expectedProductId?.let { productRepository.findById(it).get() }
        productRepository.deleteAll()

        if (product != null) {
            Assertions.assertEquals(product.name, expectedProduct.name)
        }
    }

    @Test
    fun whenSaveProduct_thenProductIsSaved() {
        val firstExpectedProduct = ProductModel(
            id = null,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        productRepository.save(firstExpectedProduct)
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

        Assertions.assertEquals(product.price, secondExpectedProduct.price)
        Assertions.assertEquals(product.name, secondExpectedProduct.name)
    }
}