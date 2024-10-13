package com.example.demo.kafka.consumer.common

import io.micrometer.core.instrument.Counter
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment

abstract class KafkaConsumer<T, U> {
    protected abstract val receivedEventsCounter: Counter
    protected abstract val processedEventsCounter: Counter

    protected abstract fun handleEvent(event: T)

    open fun handle(event: T, ack: Acknowledgment) {
        logger.info("ReserveProductEvent received: [$event]")
        receivedEventsCounter.increment()

        try {
            handleEvent(event)

            ack.acknowledge()

            processedEventsCounter.increment()
            logger.info("ReserveProductEvent processed")
        } catch (ex: Exception) {
            throw ex
        }
    }
    private val logger = LoggerFactory.getLogger(this.javaClass)
}