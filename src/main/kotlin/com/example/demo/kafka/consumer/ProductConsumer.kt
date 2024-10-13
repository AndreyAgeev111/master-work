package com.example.demo.kafka.consumer

import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ProductConsumer(val productConsumerService: ProductConsumerService) {

    @KafkaListener(topics = ["\${kafka.topics.products.topic}"])
    fun handle(event: ProductReserveEvent, ack: Acknowledgment) {
        logger.info("ReserveProductEvent received: [$event]")

        try {
            productConsumerService.processEvent(event)

            ack.acknowledge()

            logger.info("ReserveProductEvent processed")
        } catch (ex: Exception) {
            throw ex
        }
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}