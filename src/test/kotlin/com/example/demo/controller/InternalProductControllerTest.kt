package com.example.demo.controller

import com.example.demo.controller.model.Product
import com.example.demo.persistence.model.ProductModel
import com.example.demo.service.ProductService
import com.example.demo.service.exception.ProductAlreadyReservedException
import com.example.demo.service.exception.ProductNotFoundException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.mockito.Mockito.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*

@WebMvcTest(InternalProductController::class)
class InternalProductControllerTest(@Autowired val mockMvc: MockMvc) {
    @Test
    fun whenGetProduct_thenReturnProductWithStatus200() {
        val productId = 1
        val product = ProductModel(
            id = productId,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        `when`(productService.getProductById(productId)).thenReturn(Product(product))

        mockMvc.perform(get("/api/v1/products/$productId"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun whenGetProduct_thenReturnNothingWithStatus422() {
        val productId = 1

        `when`(productService.getProductById(productId)).thenThrow(ProductNotFoundException::class.java)

        mockMvc.perform(get("/api/v1/products/$productId"))
            .andExpect(status().is4xxClientError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun whenFindAllProducts_thenReturnAllProductsWithStatus200() {
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
        val jsonProducts = mapper.writeValueAsString(products)

        `when`(productService.listProducts()).thenReturn(products.map { Product(it) })

        mockMvc.perform(get("/api/v1/products/all"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(jsonProducts))
    }

    @Test
    fun whenUpsertProduct_thenReturnNothingWithStatus200() {
        val product = ProductModel(
            id = 1,
            name = UUID.randomUUID().toString(),
            price = 100,
            isAvailable = true,
            description = null
        )

        mockMvc.perform(
            post("/api/v1/products")
                .content(mapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun whenReserveProduct_thenReturnProductWithStatus200() {
        val productId = 1

        mockMvc.perform(
            post("/api/v1/products/$productId/reserve")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun whenReserveProduct_thenReturnNothingWithStatus400() {
        val productId = 1

        `when`(productService.reserveProduct(productId)).thenThrow(ProductAlreadyReservedException::class.java)

        mockMvc.perform(
            post("/api/v1/products/$productId/reserve")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun whenReserveProduct_thenReturnNothingWithStatus422() {
        val productId = 1

        `when`(productService.reserveProduct(productId)).thenThrow(ProductNotFoundException::class.java)

        mockMvc.perform(
            post("/api/v1/products/$productId/reserve")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().is4xxClientError)
    }

    @MockBean
    private lateinit var productService: ProductService
    private val mapper = jacksonObjectMapper()
}