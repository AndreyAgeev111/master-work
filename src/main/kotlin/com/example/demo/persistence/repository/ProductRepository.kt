package com.example.demo.persistence.repository

import com.example.demo.persistence.model.ProductModel
import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<ProductModel, Int>