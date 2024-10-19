package com.example.demo.controller.model

import com.example.demo.persistence.model.DeadLetterEventModel
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Model for event")
data class DeadLetterEvent(
    val id: Int,
    val payload: String,
    val createDate: Instant,
    val updateDate: Instant,
    val currentAttempt: Int,
) {
    constructor(deadLetterEventModel: DeadLetterEventModel) : this(
        id = deadLetterEventModel.id,
        payload = deadLetterEventModel.payload,
        createDate = deadLetterEventModel.createDate.toInstant(),
        updateDate = deadLetterEventModel.updateDate.toInstant(),
        currentAttempt = deadLetterEventModel.currentAttempt,
    )
}