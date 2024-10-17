package com.example.demo.persistence.model

import com.example.demo.service.model.DeadLetterEvent
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table(name = "dead_letter_events")
data class DeadLetterEventModel(
    @Id val id: Int,
    val payload: String,
    val createDate: Timestamp,
    val updateDate: Timestamp,
    val currentAttempt: Int,
) {
    constructor(event: DeadLetterEvent) : this(
        id = event.id,
        payload = event.payload,
        createDate = Timestamp.from(event.createDate),
        updateDate = Timestamp.from(event.updateDate),
        currentAttempt = event.currentAttempt
    )
}