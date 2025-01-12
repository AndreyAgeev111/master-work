package com.example.demo.service.exception

class ProductAlreadyReservedException(id: Int) : RuntimeException("Product with id $id has already been reserved")