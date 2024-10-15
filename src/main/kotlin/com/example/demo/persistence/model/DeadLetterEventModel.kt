package com.example.demo.persistence.model

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
)