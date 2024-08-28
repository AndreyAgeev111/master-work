package com.example.demo.controller

import com.example.demo.persistance.model.ProductModel
import com.example.demo.service.ProductService
import org.springframework.web.bind.annotation.*
import java.util.*

interface InternalProductController {
    fun listProducts(): List<ProductModel>
    fun findProductById(id: Int): Optional<ProductModel>
    fun createProduct(product: ProductModel)
}

@RestController
@RequestMapping("/api/v1/products")
class InternalProductControllerImpl(val service: ProductService) : InternalProductController {
    @GetMapping("")
    override fun listProducts(): List<ProductModel> = service.findProducts()

    @GetMapping("/{id}")
    override fun findProductById(@PathVariable id: Int): Optional<ProductModel> =
        service.findProductById(id)

    @PostMapping("")
    override fun createProduct(@RequestBody product: ProductModel) {
        service.upsertProduct(product)
    }
}