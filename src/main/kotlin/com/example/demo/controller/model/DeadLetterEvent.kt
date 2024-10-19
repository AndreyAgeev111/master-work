package com.example.demo.controller.model

import com.example.demo.persistence.model.DeadLetterEventModel
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Model for event")
data class DeadLetterEvent(
    @Schema(description = "ID of the event", example = "1") val id: Int,

    @Schema(description = "Payload of the event", example = """{"id": 1, product: "TV"}""") val payload: String,

    @Schema(
        description = "Date when the event was created",
        example = "2024-10-19T10:27:34.119642226Z",
        type = "string",
        format = "date-time"
    ) val createDate: Instant,

    @Schema(
        description = "Date when the event was last updated",
        example = "2024-10-19T10:27:34.119642226Z",
        type = "string",
        format = "date-time"
    ) val updateDate: Instant,

    @Schema(description = "Current attempt number of event processing", example = "1") val currentAttempt: Int
) {
    constructor(deadLetterEventModel: DeadLetterEventModel) : this(
        id = deadLetterEventModel.id,
        payload = deadLetterEventModel.payload,
        createDate = deadLetterEventModel.createDate.toInstant(),
        updateDate = deadLetterEventModel.updateDate.toInstant(),
        currentAttempt = deadLetterEventModel.currentAttempt,
    )
}