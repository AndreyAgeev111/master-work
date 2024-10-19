package com.example.demo.service

import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.controller.model.DeadLetterEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.*
import java.time.Instant
import kotlin.test.Test

class MaintenanceDeadLetterServiceTest {
    @Test
    fun whenRetryEventProcessing_thenReturnNothing() {
        val eventId = 1
        val currentDate = Instant.now()
        val reserveEvent = ProductReserveEvent(
            productId = eventId
        )
        val reserveEventString = jacksonObjectMapper().registerModules(JavaTimeModule()).writeValueAsString(reserveEvent)
        val event = DeadLetterEvent(
            id = 1,
            payload = reserveEventString,
            createDate = currentDate,
            updateDate = currentDate,
            currentAttempt = 1
        )

        `when`(deadLetterEventService.getEventById(eventId)).thenReturn(event)

        val result = maintenanceDeadLetterService.retryEventProcessing(eventId)

        verify(deadLetterEventService, atLeastOnce()).getEventById(eventId)
        verify(productConsumerService, atLeastOnce()).processEvent(reserveEvent)
        Assertions.assertEquals(result, Unit)
    }

    @Test
    fun whenDeleteEventById_thenReturnNothing() {
        val eventId = 1
        val result = maintenanceDeadLetterService.deleteEventById(eventId)

        verify(deadLetterEventService, atLeastOnce()).deleteById(eventId)
        Assertions.assertEquals(result, Unit)
    }

    @Test
    fun whenListEvents_thenReturnEvents() {
        val eventId = 1
        val currentDate = Instant.now()
        val reserveEvent = ProductReserveEvent(
            productId = eventId
        )
        val reserveEventString = jacksonObjectMapper().registerModules(JavaTimeModule()).writeValueAsString(reserveEvent)
        val event = DeadLetterEvent(
            id = 1,
            payload = reserveEventString,
            createDate = currentDate,
            updateDate = currentDate,
            currentAttempt = 1
        )
        `when`(deadLetterEventService.listEvents()).thenReturn(listOf(event))

        val result = maintenanceDeadLetterService.listEvents()

        verify(deadLetterEventService, atLeastOnce()).listEvents()
        Assertions.assertEquals(result[0].id, event.id)
    }

    private val deadLetterEventService: DeadLetterEventService = mock(DeadLetterEventService::class.java)
    private val productConsumerService: ProductConsumerService = mock(ProductConsumerService::class.java)
    private val jacksonObjectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())
    private val maintenanceDeadLetterService: MaintenanceDeadLetterService = MaintenanceDeadLetterServiceImpl(deadLetterEventService, productConsumerService, jacksonObjectMapper)
}