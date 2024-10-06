package com.example.demo.service.exception

class ProductAlreadyReservedException(id: Int) : Exception("Product with id $id has already been reserved")