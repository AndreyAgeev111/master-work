package com.example.demo.service

import com.example.demo.kafka.producer.ProductProducer
import com.example.demo.persistence.model.ProductModel
import com.example.demo.persistence.repository.ProductRepository
import com.example.demo.service.exception.ProductAlreadyReservedException
import com.example.demo.service.exception.ProductNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

interface ProductService {
    fun findProducts(): List<ProductModel>
    fun getProductById(id: Int): ProductModel
    fun upsertProduct(product: ProductModel)
    fun reserveProduct(id: Int)
}

@Service("productService")
class ProductServiceImpl(
    val db: ProductRepository,
    val productProducer: ProductProducer
) : ProductService {
    override fun findProducts(): List<ProductModel> = db.findAll().toList()

    override fun getProductById(id: Int): ProductModel = getProduct(id)

    override fun upsertProduct(product: ProductModel) {
        db.save(product)
    }

    override fun reserveProduct(id: Int) {
        getProduct(id)
            .also {
                if (!it.isAvailable!!) {
                    logger.error("Product with id $id has already been reserved")
                    throw ProductAlreadyReservedException(id)
                }
            }
            .let { db.save(it.copy(isAvailable = false)) }
        productProducer.sendProductReservedEvent(id)
    }

    private fun getProduct(id: Int): ProductModel = db.findById(id).getOrElse {
        logger.error("Product with id $id was not found")
        throw ProductNotFoundException(id)
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}