package com.example.demo.kafka.producer.common

import io.micrometer.core.instrument.Counter
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate

abstract class KafkaProducer<K : Any, T>(
    private val kafkaTemplate: KafkaTemplate<K, T>,
) {
    protected abstract val topic: String
    protected abstract val sentEventsCounter: Counter

    fun send(key: K, event: T) {
        kafkaTemplate.send(topic, key, event)
        sentEventsCounter.increment()
        logger.info("Event with key = $key sent")
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}