package com.example.demo.controller

import com.example.demo.controller.model.Product
import com.example.demo.controller.model.error.ErrorResponse
import com.example.demo.persistance.model.ProductModel
import com.example.demo.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

interface InternalProductController {
    fun listProducts(): List<Product>
    fun getProductById(id: Int): Product
    fun upsertProduct(product: Product)
    fun reserveProduct(id: Int)
}

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API")
class InternalProductControllerImpl(val service: ProductService) : InternalProductController {
    @GetMapping(path = ["/all"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "List all products")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = Array<Product>::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal error",
                content = [Content()]
            )
        ]
    )
    override fun listProducts(): List<Product> =
        service.findProducts()
            .map { Product(it) }


    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Get product by id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = Product::class))]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Product was not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal error",
                content = [Content(schema = Schema())]
            )
        ]
    )
    override fun getProductById(@PathVariable id: Int): Product =
        Product(service.getProductById(id))


    @PostMapping(path = [""], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Upsert product")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK", content = [Content()]),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal error",
                content = [Content()]
            )
        ]
    )
    override fun upsertProduct(@RequestBody(required = true) product: Product) {
        service.upsertProduct(ProductModel(product))
    }

    @PostMapping(path = ["/{id}/reserve"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Reserve product by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK", content = [Content()]),
            ApiResponse(
                responseCode = "400",
                description = "Product has already been reserved",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Product was not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal error",
                content = [Content(schema = Schema())]
            )
        ]
    )
    override fun reserveProduct(@PathVariable id: Int) {
        service.reserveProduct(id)
    }
}