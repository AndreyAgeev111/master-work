package com.example.demo.service.exception

class ProductNotFoundException(id: Int) : RuntimeException("Product with id $id not found")