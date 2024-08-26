package com.example.demo.persistance.service

import com.example.demo.persistance.model.ProductModel
import com.example.demo.persistance.repository.ProductRepository
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

        val resultProduct: Optional<ProductModel> = productService.findProductById(productId)

        verify(productRepository, atLeastOnce()).findById(productId)
        Assertions.assertEquals(resultProduct, emptyProduct)
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

        val resultProduct: Optional<ProductModel> = productService.findProductById(productId)

        verify(productRepository, atLeastOnce()).findById(productId)
        Assertions.assertEquals(resultProduct.get().name, product.name)
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
        Assertions.assertEquals(result, Unit)
    }

    private val productRepository: ProductRepository = mock(ProductRepository::class.java)
    private val productService: ProductService = ProductServiceImpl(productRepository)
}