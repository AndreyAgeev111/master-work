package com.example.demo.persistance.repository

import com.example.demo.persistance.model.ProductModel
import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<ProductModel, Int>