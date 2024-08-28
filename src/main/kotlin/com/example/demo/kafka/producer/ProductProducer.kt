package com.example.demo.kafka.producer

import com.example.demo.kafka.config.ProductConfiguration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductProducer(
    private val productKafkaTemplate: KafkaTemplate<String, String>,
    private val productConfiguration: ProductConfiguration
) {
    fun sendStringMessage(message: String) {
        productKafkaTemplate.send(productConfiguration.topic, message)
    }
}