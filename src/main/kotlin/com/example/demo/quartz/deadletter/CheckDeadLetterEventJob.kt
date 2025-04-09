package com.example.demo.quartz.deadletter

import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.quartz.deadletter.configuration.CheckDeadLetterRetryConfiguration
import com.example.demo.quartz.deadletter.util.TimeService
import com.example.demo.service.DeadLetterEventService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

@Component
class CheckDeadLetterEventJob(
    private val deadLetterEventService: DeadLetterEventService,
    private val productConsumerService: ProductConsumerService,
    private val timeService: TimeService,
    private val jacksonObjectMapper: ObjectMapper,
    private val checkDeadLetterRetryConfiguration: CheckDeadLetterRetryConfiguration
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        val currentTime = Instant.now()

        deadLetterEventService.listEvents()
            .filter {
                currentTime.minus(getRetryIntervals()[it.currentAttempt - 1]).isAfter(it.updateDate) && it.currentAttempt < 12
            }
            .map { jacksonObjectMapper.readValue<ProductReserveEvent>(it.payload) }
            .map {
                runCatching {
                    productConsumerService.processEvent(it)
                    logger.info("Event with id = ${it.productId} processed successfully")
                }.onFailure { ex ->
                    deadLetterEventService.getEventById(it.productId)
                        .let { event ->
                            deadLetterEventService.upsertEvent(
                                event.copy(
                                    currentAttempt = event.currentAttempt + 1,
                                    updateDate = currentTime
                                )
                            )
                        }
                    logger.error("Error while processing event", ex)
                }
            }
    }

    private fun getRetryIntervals(): List<Duration> =
        checkDeadLetterRetryConfiguration.retryIntervals
            .map { timeService.parseInterval(it) }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}