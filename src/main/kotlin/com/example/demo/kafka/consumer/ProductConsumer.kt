package com.example.demo.kafka.consumer

import com.example.demo.kafka.model.ProductReserveEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProductConsumer {

    @KafkaListener(topics = ["\${kafka.topics.products.topic}"])
    fun firstListener(event: ProductReserveEvent) {
        logger.info("Event received: [$event]")
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}