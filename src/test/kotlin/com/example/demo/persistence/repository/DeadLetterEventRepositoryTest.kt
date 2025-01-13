package com.example.demo.persistence.repository

import com.example.demo.persistence.model.DeadLetterEventModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.util.ResourceUtils
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Timestamp
import java.time.Instant

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@TestPropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application-test.properties")
class DeadLetterEventRepositoryTest {
    @Autowired
    lateinit var deadLetterEventRepository: DeadLetterEventRepository

    @Test
    fun whenInsertEvent_thenEventIsInserted() {
        val expectedEvent = DeadLetterEventModel(
            id = 1,
            payload = "a",
            createDate = Timestamp.from(Instant.now()),
            updateDate = Timestamp.from(Instant.now()),
            currentAttempt = 1
        )

        deadLetterEventRepository.insert(expectedEvent)
        val expectedEventId = deadLetterEventRepository.findAll().iterator().next().id
        deadLetterEventRepository.deleteAll()

        assertEquals(expectedEventId, expectedEvent.id)
    }

    @Test
    fun whenFindEventById_returnsEventById() {
        val expectedEvent = DeadLetterEventModel(
            id = 1,
            payload = "a",
            createDate = Timestamp.from(Instant.now()),
            updateDate = Timestamp.from(Instant.now()),
            currentAttempt = 1
        )

        deadLetterEventRepository.insert(expectedEvent)
        val expectedEventId = deadLetterEventRepository.findAll().iterator().next().id
        val event = deadLetterEventRepository.findById(expectedEventId).get()
        deadLetterEventRepository.deleteAll()

        assertEquals(expectedEvent.currentAttempt, event.currentAttempt)
    }
}