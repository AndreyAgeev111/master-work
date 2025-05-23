package com.example.demo.service

import com.example.demo.controller.model.Product
import com.example.demo.kafka.producer.ProductProducer
import com.example.demo.persistence.model.ProductModel
import com.example.demo.persistence.repository.ProductRepository
import com.example.demo.service.exception.ProductAlreadyReservedException
import com.example.demo.service.exception.ProductNotFoundException
import org.junit.jupiter.api.Assertions.*
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

        val resultProducts: List<Product> = productService.listProducts()

        verify(productRepository, atLeastOnce()).findAll()
        assertEquals(resultProducts.size, products.size)
        assertEquals(resultProducts.first().price, products.first().price)
    }

    @Test
    fun whenFindAllProducts_thenReturnEmptyListProducts() {
        val products = emptyList<ProductModel>()

        `when`(productRepository.findAll()).thenReturn(products)

        val resultProducts: List<Product> = productService.listProducts()

        verify(productRepository, atLeastOnce()).findAll()
        assertTrue(resultProducts.isEmpty())
    }

    @Test
    fun whenGetProduct_thenThrowException() {
        val productId = 1
        val emptyProduct = Optional.empty<ProductModel>()

        `when`(productRepository.findById(productId)).thenReturn(emptyProduct)

        assertThrows(ProductNotFoundException::class.java) {
            productService.getProductById(productId)
        }
    }

    @Test
    fun whenGetProduct_thenReturnProduct() {
        val productId = 1
        val product = ProductModel(
            id = productId,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        `when`(productRepository.findById(productId)).thenReturn(Optional.of(product))

        val resultProduct: Product = productService.getProductById(productId)

        verify(productRepository, atLeastOnce()).findById(productId)
        assertEquals(resultProduct.name, product.name)
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

        `when`(productRepository.findById(product.id)).thenReturn(Optional.of(product))

        val result = productService.upsertProduct(Product(product))

        verify(productRepository, atLeastOnce()).save(product)
        assertEquals(Unit, result)
    }

    @Test
    fun whenReserveProduct_thenReturnNothing() {
        val productId = 1
        val product = ProductModel(
            id = productId,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        `when`(productRepository.findById(productId)).thenReturn(Optional.of(product))

        val result = productService.reserveProduct(productId)

        verify(productRepository, atLeastOnce()).findById(productId)
        verify(productRepository, atLeastOnce()).save(product.copy(isAvailable = false))
        assertEquals(Unit, result)
    }

    @Test
    fun whenReserveProduct_thenFindProduct_thenReturnEmptyProduct() {
        val productId = 1
        val emptyProduct = Optional.empty<ProductModel>()

        `when`(productRepository.findById(productId)).thenReturn(emptyProduct)

        assertThrows(ProductNotFoundException::class.java) {
            productService.reserveProduct(productId)
        }
    }

    @Test
    fun whenReserveProduct_thenFindProduct_thenReturnAlreadyReservedProduct() {
        val productId = 1
        val product = ProductModel(
            id = productId,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = false,
            description = null
        )

        `when`(productRepository.findById(productId)).thenReturn(Optional.of(product))

        assertThrows(ProductAlreadyReservedException::class.java) {
            productService.reserveProduct(productId)
        }
    }


    private val productRepository: ProductRepository = mock(ProductRepository::class.java)
    private val productProducer: ProductProducer = mock(ProductProducer::class.java)
    private val productService: ProductService = ProductServiceImpl(productRepository, productProducer)
}