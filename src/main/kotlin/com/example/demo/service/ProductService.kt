package com.example.demo.service

import com.example.demo.kafka.producer.ProductProducer
import com.example.demo.persistance.model.ProductModel
import com.example.demo.persistance.repository.ProductRepository
import com.example.demo.service.exception.ProductNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

interface ProductService {
    fun findProducts(): List<ProductModel>
    fun getProductById(id: Int): ProductModel
    fun upsertProduct(message: ProductModel)
}

@Service("productService")
class ProductServiceImpl(
    val db: ProductRepository,
    val productProducer: ProductProducer
) : ProductService {
    override fun findProducts(): List<ProductModel> = db.findAll().toList()

    override fun getProductById(id: Int): ProductModel = db.findById(id).getOrElse {
        throw ProductNotFoundException(id)
    }

    override fun upsertProduct(message: ProductModel) {
        db.save(message)
        productProducer.sendStringMessage(message.toString())
    }
}