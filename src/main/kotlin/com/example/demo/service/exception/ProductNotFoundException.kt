package com.example.demo.service.exception

class ProductNotFoundException(id: Int) : Exception("Product with id $id not found")