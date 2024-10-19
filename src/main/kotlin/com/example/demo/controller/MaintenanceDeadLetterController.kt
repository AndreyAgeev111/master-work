package com.example.demo.controller

import com.example.demo.service.MaintenanceDeadLetterService
import com.example.demo.controller.model.DeadLetterEvent
import com.example.demo.controller.model.error.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

interface MaintenanceDeadLetterController {
    fun retryEventProcessing(id: Int)
    fun deleteEventById(id: Int)
    fun listEvents(): List<DeadLetterEvent>
}

@RestController
@RequestMapping("/api/v1/maintenance/dead-letter")
@Tag(name = "Maintenance API")
class MaintenanceDeadLetterControllerImpl(private val maintenanceDeadLetterService: MaintenanceDeadLetterService) :
    MaintenanceDeadLetterController {
    @PostMapping(path = ["/{id}/retry"])
    @Operation(description = "Retries event processing from queue by id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "417",
                description = "Event processing ended with an error",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Event not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal error",
                content = [Content()]
            )
        ]
    )
    override fun retryEventProcessing(@PathVariable id: Int) {
        maintenanceDeadLetterService.retryEventProcessing(id)
    }

    @DeleteMapping(path = ["/{id}/delete"])
    @Operation(description = "Delete event from queue by id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Event not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal error",
                content = [Content()]
            )
        ]
    )
    override fun deleteEventById(@PathVariable id: Int) {
        maintenanceDeadLetterService.deleteEventById(id)
    }

    @GetMapping(path = ["/all"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "List all events from queue")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(array = ArraySchema(schema = Schema(implementation = DeadLetterEvent::class)))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal error",
                content = [Content()]
            )
        ]
    )
    override fun listEvents() = maintenanceDeadLetterService.listEvents()
}