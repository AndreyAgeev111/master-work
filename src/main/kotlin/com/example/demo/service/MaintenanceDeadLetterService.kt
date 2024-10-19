package com.example.demo.service

import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.service.exception.EventProcessingFailedException
import com.example.demo.controller.model.DeadLetterEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service

interface MaintenanceDeadLetterService {
    fun retryEventProcessing(id: Int)
    fun deleteEventById(id: Int)
    fun listEvents(): List<DeadLetterEvent>
}

@Service("maintenanceDeadLetterService")
class MaintenanceDeadLetterServiceImpl(
    private val deadLetterEventService: DeadLetterEventService,
    private val productConsumerService: ProductConsumerService,
    private val jacksonObjectMapper: ObjectMapper
) : MaintenanceDeadLetterService {
    override fun retryEventProcessing(id: Int) {
        try {
            deadLetterEventService.getEventById(id)
                .let {
                    jacksonObjectMapper.readValue<ProductReserveEvent>(it.payload)
                }
                .let { productConsumerService.processEvent(it) }
        } catch (ex: Exception) {
            throw EventProcessingFailedException(id)
        }
    }

    override fun deleteEventById(id: Int) {
        deadLetterEventService.deleteById(id)
    }

    override fun listEvents(): List<DeadLetterEvent> = deadLetterEventService.listEvents()
}