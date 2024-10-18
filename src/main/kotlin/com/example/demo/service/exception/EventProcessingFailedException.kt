package com.example.demo.service.exception

class EventProcessingFailedException(id: Int) : Exception("Event with id $id processing ended with an error")