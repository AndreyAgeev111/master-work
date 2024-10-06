package com.example.demo.kafka.producer

import com.example.demo.kafka.config.ProductConfiguration
import com.example.demo.kafka.model.ProductReserveEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductProducer(
    private val productKafkaTemplate: KafkaTemplate<Int, ProductReserveEvent>,
    private val productConfiguration: ProductConfiguration
) {
    fun sendProductReservedEvent(id: Int) {
        productKafkaTemplate.send(productConfiguration.topic, id, ProductReserveEvent(id))
        logger.info("Event with key = $id sent")
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}