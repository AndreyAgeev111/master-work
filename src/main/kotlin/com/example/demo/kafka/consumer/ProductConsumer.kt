package com.example.demo.kafka.consumer

import com.example.demo.kafka.config.ProductConfiguration
import com.example.demo.kafka.consumer.common.KafkaConsumer
import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.service.DeadLetterEventService
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.MeterRegistry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ProductConsumer(
    private val productConsumerService: ProductConsumerService,
    productConfiguration: ProductConfiguration,
    meterRegistry: MeterRegistry,
    deadLetterEventService: DeadLetterEventService,
    jacksonObjectMapper: ObjectMapper,
) : KafkaConsumer<ProductReserveEvent, Any?>(meterRegistry, deadLetterEventService, jacksonObjectMapper) {
    override val topic: String = productConfiguration.topic
    override val isSendToDeadLetterQueue: Boolean = productConfiguration.isSendToDeadLetterQueue
    override val deadLetterQueueLimit: Long = productConfiguration.deadLetterQueueLimit

    @KafkaListener(topics = ["\${kafka.topics.products.topic}"])
    override fun handle(record: ConsumerRecord<Int, ProductReserveEvent>, ack: Acknowledgment) {
        super.handle(record, ack)
    }

    override fun handleEvent(event: ProductReserveEvent) {
        productConsumerService.processEvent(event)
    }
}