package com.example.demo.service.model

import com.example.demo.persistence.model.DeadLetterEventModel
import java.time.Instant

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