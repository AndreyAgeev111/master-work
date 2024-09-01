package com.example.demo.service

import com.example.demo.kafka.producer.ProductProducer
import com.example.demo.persistance.model.ProductModel
import com.example.demo.persistance.repository.ProductRepository
import com.example.demo.service.exception.ProductNotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

class ProductServiceTest {
    @Test
    fun whenFindAllProducts_thenReturnListProducts() {
        val productFirst = ProductModel(
            id = 1,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )
        val productSecond = ProductModel(
            id = 2,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )
        val products = listOf(productFirst, productSecond)

        `when`(productRepository.findAll()).thenReturn(products)

        val resultProducts: List<ProductModel> = productService.findProducts()

        verify(productRepository, atLeastOnce()).findAll()
        Assertions.assertEquals(resultProducts.size, products.size)
        Assertions.assertEquals(resultProducts.first().price, products.first().price)
    }

    @Test
    fun whenFindAllProducts_thenReturnEmptyListProducts() {
        val products = emptyList<ProductModel>()

        `when`(productRepository.findAll()).thenReturn(products)

        val resultProducts: List<ProductModel> = productService.findProducts()

        verify(productRepository, atLeastOnce()).findAll()
        Assertions.assertTrue(resultProducts.isEmpty())
    }


    @Test
    fun whenFindProduct_thenReturnEmptyProduct() {
        val productId = 1
        val emptyProduct = Optional.empty<ProductModel>()

        `when`(productRepository.findById(productId)).thenReturn(emptyProduct)

        Assertions.assertThrows(ProductNotFoundException::class.java) {
            productService.getProductById(productId)
        }
    }

    @Test
    fun whenFindProduct_thenReturnProduct() {
        val productId = 1
        val product = ProductModel(
            id = productId,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        `when`(productRepository.findById(productId)).thenReturn(Optional.of(product))

        val resultProduct: ProductModel = productService.getProductById(productId)

        verify(productRepository, atLeastOnce()).findById(productId)
        Assertions.assertEquals(resultProduct.name, product.name)
    }

    @Test
    fun whenUpsertProduct_thenReturnNothing() {
        val product = ProductModel(
            id = 1,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        val result = productService.upsertProduct(product)

        verify(productRepository, atLeastOnce()).save(product)
        verify(productProducer, atLeastOnce()).sendStringMessage(product.toString())
        Assertions.assertEquals(result, Unit)
    }

    private val productRepository: ProductRepository = mock(ProductRepository::class.java)
    private val productProducer: ProductProducer = mock(ProductProducer::class.java)
    private val productService: ProductService = ProductServiceImpl(productRepository, productProducer)
}