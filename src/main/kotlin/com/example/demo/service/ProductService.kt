package com.example.demo.service

import com.example.demo.kafka.producer.ProductProducer
import com.example.demo.persistance.model.ProductModel
import com.example.demo.persistance.repository.ProductRepository
import org.springframework.stereotype.Service
import java.util.*

interface ProductService {
    fun findProducts(): List<ProductModel>
    fun findProductById(id: Int): Optional<ProductModel>
    fun upsertProduct(message: ProductModel)
}

@Service("productService")
class ProductServiceImpl(
    val db: ProductRepository,
    val productProducer: ProductProducer
) : ProductService {
    override fun findProducts(): List<ProductModel> = db.findAll().toList()

    override fun findProductById(id: Int): Optional<ProductModel> = db.findById(id)

    override fun upsertProduct(message: ProductModel) {
        db.save(message)
        productProducer.sendStringMessage(message.toString())
    }
}