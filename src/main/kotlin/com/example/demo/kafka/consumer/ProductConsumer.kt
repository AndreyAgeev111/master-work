package com.example.demo.kafka.consumer

import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProductConsumer(val productConsumerService: ProductConsumerService) {

    @KafkaListener(topics = ["\${kafka.topics.products.topic}"])
    fun handle(event: ProductReserveEvent) {
        logger.info("ReserveProductEvent received: [$event]")

        productConsumerService.processEvent(event)

        logger.info("ReserveProductEvent processed")
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}