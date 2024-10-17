package com.example.demo.service

import com.example.demo.controller.model.Product
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.kafka.producer.ProductProducer
import com.example.demo.persistence.model.ProductModel
import com.example.demo.persistence.repository.ProductRepository
import com.example.demo.service.exception.ProductAlreadyReservedException
import com.example.demo.service.exception.ProductNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

interface ProductService {
    fun listProducts(): List<Product>
    fun getProductById(id: Int): Product
    fun upsertProduct(product: Product)
    fun reserveProduct(id: Int)
}

@Service("productService")
class ProductServiceImpl(
    val db: ProductRepository,
    val productProducer: ProductProducer
) : ProductService {
    override fun listProducts(): List<Product> = db.findAll().toList().map { Product(it) }

    override fun getProductById(id: Int): Product = Product(getProduct(id))

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun upsertProduct(product: Product) {
        db.findById(product.id).ifPresentOrElse(
            { db.save(ProductModel(product)) },
            { db.insert(ProductModel(product)) }
        )
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
        productProducer.send(id, ProductReserveEvent(id))
    }

    private fun getProduct(id: Int): ProductModel = db.findById(id).getOrElse {
        logger.error("Product with id $id was not found")
        throw ProductNotFoundException(id)
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}