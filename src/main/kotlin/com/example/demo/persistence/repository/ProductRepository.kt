package com.example.demo.persistence.repository

import com.example.demo.persistence.model.ProductModel
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ProductRepository : CrudRepository<ProductModel, Int> {
    @Modifying
    @Transactional
    @Query("INSERT INTO products VALUES (:#{#product.id}, :#{#product.name}, :#{#product.price}, :#{#product.isAvailable}, :#{#product.description})")
    fun insert(@Param("product") product: ProductModel)
}