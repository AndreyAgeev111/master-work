package com.example.demo.kafka.consumer

import com.example.demo.kafka.config.ProductConfiguration
import com.example.demo.kafka.consumer.common.KafkaConsumer
import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ProductConsumer(
    private val productConsumerService: ProductConsumerService,
    meterRegistry: MeterRegistry
) : KafkaConsumer<ProductReserveEvent, Any?>() {
    @KafkaListener(topics = ["\${kafka.topics.products.topic}"])
    override fun handle(event: ProductReserveEvent, ack: Acknowledgment) {
        super.handle(event, ack)
    }

    override fun handleEvent(event: ProductReserveEvent) {
        productConsumerService.processEvent(event)
    }

    override val receivedEventsCounter: Counter = meterRegistry.counter("kafka_product_reserved_event_received")
    override val processedEventsCounter: Counter = meterRegistry.counter("kafka_product_reserved_event_processed")
}