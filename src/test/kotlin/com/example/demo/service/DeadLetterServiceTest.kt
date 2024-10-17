package com.example.demo.service

import com.example.demo.persistence.model.DeadLetterEventModel
import com.example.demo.persistence.repository.DeadLetterEventRepository
import com.example.demo.service.exception.EventNotFoundException
import com.example.demo.service.model.DeadLetterEvent
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.Instant
import java.util.*

class DeadLetterServiceTest {
    @Test
    fun whenGetEvent_thenThrowException() {
        val eventId = 1
        val emptyProduct = Optional.empty<DeadLetterEventModel>()

        `when`(deadLetterEventRepository.findById(eventId)).thenReturn(emptyProduct)

        Assertions.assertThrows(EventNotFoundException::class.java) {
            deadLetterEventService.getEventById(eventId)
        }
    }

    @Test
    fun whenGetEvent_thenReturnEvent() {
        val eventId = 1
        val currentDate = Instant.now()
        val event = DeadLetterEvent(
            id = 1,
            payload = "a",
            createDate = currentDate,
            updateDate = currentDate,
            currentAttempt = 1
        )
        val model = DeadLetterEventModel(event)

        `when`(deadLetterEventRepository.findById(eventId)).thenReturn(Optional.of(model))

        val resultEvent: DeadLetterEvent = deadLetterEventService.getEventById(eventId)

        verify(deadLetterEventRepository, atLeastOnce()).findById(eventId)
        Assertions.assertEquals(resultEvent.currentAttempt, event.currentAttempt)
    }

    @Test
    fun whenUpsertEvent_thenReturnNothing() {
        val currentDate = Instant.now()
        val event = DeadLetterEvent(
            id = 1,
            payload = "a",
            createDate = currentDate,
            updateDate = currentDate,
            currentAttempt = 1
        )
        val model = DeadLetterEventModel(event)

        `when`(deadLetterEventRepository.findById(event.id)).thenReturn(Optional.of(model))

        val result = deadLetterEventService.upsertEvent(event)

        verify(deadLetterEventRepository, atLeastOnce()).save(model)
        Assertions.assertEquals(result, Unit)
    }

    private val deadLetterEventRepository: DeadLetterEventRepository = mock(DeadLetterEventRepository::class.java)
    private val meterRegistry: MeterRegistry = mock(MeterRegistry::class.java)
    private val deadLetterEventService: DeadLetterEventService = DeadLetterEventsServiceImpl(deadLetterEventRepository, meterRegistry)
}