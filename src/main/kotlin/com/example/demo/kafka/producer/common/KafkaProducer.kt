package com.example.demo.kafka.producer.common

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate

abstract class KafkaProducer<K : Any, T>(
    private val kafkaTemplate: KafkaTemplate<K, T>,
    meterRegistry: MeterRegistry
) {
    protected abstract val topic: String

    fun send(key: K, event: T) {
        try {
            kafkaTemplate.send(topic, key, event)

            logger.info("Event with key = $key sent")
            sentEventsCounter.increment()
        } catch (ex: Exception) {
            logger.error("Event sending ended with an error", ex)
            failedToSentEventsCounter.increment()

            throw ex
        }
    }

    private val sentEventsCounter: Counter by lazy {
        Counter
            .builder("kafka_event_sent")
            .tag("topic", topic)
            .register(meterRegistry)
    }
    private val failedToSentEventsCounter: Counter by lazy {
        Counter
            .builder("kafka_event_sent_failed")
            .tag("topic", topic)
            .register(meterRegistry)
    }
    private val logger = LoggerFactory.getLogger(this.javaClass)
}