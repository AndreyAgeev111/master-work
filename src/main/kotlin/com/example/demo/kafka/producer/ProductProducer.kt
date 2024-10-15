package com.example.demo.kafka.producer

import com.example.demo.kafka.config.ProductConfiguration
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.kafka.producer.common.KafkaProducer
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductProducer(
    productKafkaTemplate: KafkaTemplate<Int, ProductReserveEvent>,
    productConfiguration: ProductConfiguration,
    meterRegistry: MeterRegistry
) : KafkaProducer<Int, ProductReserveEvent>(productKafkaTemplate, meterRegistry) {
    override val topic: String = productConfiguration.topic
}