package com.example.demo.service.exception

class EventNotFoundException(id: Int) : Exception("Event with id $id not found")