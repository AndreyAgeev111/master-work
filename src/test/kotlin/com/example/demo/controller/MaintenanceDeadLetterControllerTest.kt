package com.example.demo.controller

import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.service.MaintenanceDeadLetterService
import com.example.demo.controller.model.DeadLetterEvent
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant

@WebMvcTest(MaintenanceDeadLetterController::class)
class MaintenanceDeadLetterControllerTest(@Autowired val mockMvc: MockMvc) {
    @Test
    fun whenRetryEventProcessingById_thenReturnNothingWithStatus200() {
        val eventId = 1

        mockMvc.perform(post("/api/v1/maintenance/dead-letter/$eventId/retry"))
            .andExpect(status().isOk)
    }

    @Test
    fun whenDeleteEventById_thenReturnNothingWithStatus200() {
        val eventId = 1

        mockMvc.perform(delete("/api/v1/maintenance/dead-letter/$eventId/delete"))
            .andExpect(status().isOk)
    }

    @Test
    fun whenListEvents_thenReturnListWithStatus200() {
        val currentDate = Instant.now()
        val reserveEvent = ProductReserveEvent(
            productId = 1
        )
        val reserveEventString = mapper.writeValueAsString(reserveEvent)
        val eventFirst = DeadLetterEvent(
            id = 1,
            payload = reserveEventString,
            createDate = currentDate,
            updateDate = currentDate,
            currentAttempt = 1
        )
        val eventSecond = DeadLetterEvent(
            id = 2,
            payload = reserveEventString,
            createDate = currentDate,
            updateDate = currentDate,
            currentAttempt = 1
        )
        val events = listOf(eventFirst, eventSecond)
        val jsonEvents = mapper.writeValueAsString(events)

        `when`(maintenanceDeadLetterService.listEvents()).thenReturn(events)

        mockMvc.perform(get("/api/v1/maintenance/dead-letter/all"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(jsonEvents))
    }

    @MockBean
    private lateinit var maintenanceDeadLetterService: MaintenanceDeadLetterService
    private val mapper = jacksonObjectMapper().registerModules(JavaTimeModule()).disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
}