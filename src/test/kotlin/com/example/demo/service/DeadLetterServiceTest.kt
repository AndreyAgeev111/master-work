package com.example.demo.service

import com.example.demo.persistence.model.DeadLetterEventModel
import com.example.demo.persistence.repository.DeadLetterEventRepository
import com.example.demo.service.exception.EventNotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.sql.Timestamp
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
    fun whenGetProduct_thenReturnProduct() {
        val eventId = 1
        val event = DeadLetterEventModel(
            id = 1,
            payload = "a",
            createDate = Timestamp.from(Instant.now()),
            updateDate = Timestamp.from(Instant.now()),
            currentAttempt = 1
        )

        `when`(deadLetterEventRepository.findById(eventId)).thenReturn(Optional.of(event))

        val resultEvent: DeadLetterEventModel = deadLetterEventService.getEventById(eventId)

        verify(deadLetterEventRepository, atLeastOnce()).findById(eventId)
        Assertions.assertEquals(resultEvent.currentAttempt, event.currentAttempt)
    }

    @Test
    fun whenUpsertProduct_thenReturnNothing() {
        val event = DeadLetterEventModel(
            id = 1,
            payload = "a",
            createDate = Timestamp.from(Instant.now()),
            updateDate = Timestamp.from(Instant.now()),
            currentAttempt = 1
        )

        `when`(deadLetterEventRepository.findById(event.id)).thenReturn(Optional.of(event))

        val result = deadLetterEventService.upsertEvent(event)

        verify(deadLetterEventRepository, atLeastOnce()).save(event)
        Assertions.assertEquals(result, Unit)
    }

    private val deadLetterEventRepository: DeadLetterEventRepository = mock(DeadLetterEventRepository::class.java)
    private val deadLetterEventService: DeadLetterEventsService = DeadLetterEventsServiceImpl(deadLetterEventRepository)
}