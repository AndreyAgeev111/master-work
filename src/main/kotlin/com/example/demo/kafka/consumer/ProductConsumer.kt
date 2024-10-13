package com.example.demo.kafka.consumer

import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ProductConsumer(private val productConsumerService: ProductConsumerService, meterRegistry: MeterRegistry) {

    @KafkaListener(topics = ["\${kafka.topics.products.topic}"])
    fun handle(event: ProductReserveEvent, ack: Acknowledgment) {
        logger.info("ReserveProductEvent received: [$event]")
        receivedEventsCounter.increment()

        try {
            productConsumerService.processEvent(event)

            ack.acknowledge()

            processedEventsCounter.increment()
            logger.info("ReserveProductEvent processed")
        } catch (ex: Exception) {
            throw ex
        }
    }

    private val receivedEventsCounter = meterRegistry.counter("kafka_product_reserved_event_received")
    private val processedEventsCounter = meterRegistry.counter("kafka_product_reserved_event_processed")
    private val logger = LoggerFactory.getLogger(this.javaClass)
}