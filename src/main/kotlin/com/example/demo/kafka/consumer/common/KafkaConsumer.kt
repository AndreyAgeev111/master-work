package com.example.demo.kafka.consumer.common

import com.example.demo.service.DeadLetterEventService
import com.example.demo.service.model.DeadLetterEvent
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment
import java.time.Instant

abstract class KafkaConsumer<V>(
    meterRegistry: MeterRegistry,
    private val deadLetterEventService: DeadLetterEventService
) {
    protected abstract val topic: String
    protected abstract val isSendToDeadLetterQueue: Boolean

    protected abstract fun handleEvent(event: V)

    open fun handle(record: ConsumerRecord<Int, V>, ack: Acknowledgment) {
        logger.info("Event with key = ${record.key()} received: [${record.value()}]")
        receivedEventsCounter.increment()

        try {
            handleEvent(record.value())
            ack.acknowledge()

            logger.info("Event processed")
            processedEventsCounter.increment()
        } catch (ex: Exception) {
            logger.error("Event processing ended with an error", ex)
            failedToProcessEventsCounter.increment()

            if (isSendToDeadLetterQueue) {
                val currentDate = Instant.now()
                val deadLetterEvent = DeadLetterEvent(
                    id = record.key(),
                    payload = jacksonObjectMapper()
                        .registerModules(JavaTimeModule())
                        .writeValueAsString(record.value()),
                    createDate = currentDate,
                    updateDate = currentDate,
                    currentAttempt = 1
                )
                deadLetterEventService.upsertEvent(deadLetterEvent)

                ack.acknowledge()
                logger.error("Event with key = ${record.key()} sent to dead-letter queue")
                processedEventsCounter.increment()
            } else {
                throw ex
            }
        }
    }

    private val receivedEventsCounter: Counter by lazy {
        Counter.builder("kafka_event_received").tag("topic", topic).register(meterRegistry)
    }
    private val processedEventsCounter: Counter by lazy {
        Counter.builder("kafka_event_processed").tag("topic", topic).register(meterRegistry)
    }
    private val failedToProcessEventsCounter: Counter by lazy {
        Counter.builder("kafka_event_process_failed").tag("topic", topic).register(meterRegistry)
    }
    private val logger = LoggerFactory.getLogger(this.javaClass)
}