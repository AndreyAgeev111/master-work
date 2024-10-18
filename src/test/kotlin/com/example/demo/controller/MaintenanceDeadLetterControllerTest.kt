package com.example.demo.controller

import com.example.demo.service.MaintenanceDeadLetterService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

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

    @MockBean
    private lateinit var maintenanceDeadLetterService: MaintenanceDeadLetterService
}