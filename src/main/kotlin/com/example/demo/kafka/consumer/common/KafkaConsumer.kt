package com.example.demo.kafka.consumer.common

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment

abstract class KafkaConsumer<T, U>(meterRegistry: MeterRegistry) {
    protected abstract val topic: String

    protected abstract fun handleEvent(event: T)

    open fun handle(event: T, ack: Acknowledgment) {
        logger.info("Event received: [$event]")
        receivedEventsCounter.increment()

        try {
            handleEvent(event)
            ack.acknowledge()

            logger.info("Event processed")
            processedEventsCounter.increment()
        } catch (ex: Exception) {
            logger.error("Event processing ended with an error", ex)
            failedToProcessEventsCounter.increment()

            throw ex
        }
    }

    private val receivedEventsCounter: Counter by lazy {
        Counter
            .builder("kafka_event_received")
            .tag("topic", topic)
            .register(meterRegistry)
    }
    private val processedEventsCounter: Counter by lazy {
        Counter
            .builder("kafka_event_processed")
            .tag("topic", topic)
            .register(meterRegistry)
    }
    private val failedToProcessEventsCounter: Counter by lazy {
        Counter
            .builder("kafka_event_process_failed")
            .tag("topic", topic)
            .register(meterRegistry)
    }
    private val logger = LoggerFactory.getLogger(this.javaClass)
}