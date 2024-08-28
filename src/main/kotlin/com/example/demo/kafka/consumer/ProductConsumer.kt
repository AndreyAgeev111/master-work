package com.example.demo.kafka.consumer

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProductConsumer {

    @KafkaListener(topics = ["\${kafka.topics.products.topic}"])
    fun firstListener(message: String) {
        logger.info("Message received: [$message]")
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}