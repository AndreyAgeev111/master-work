package com.example.demo.kafka.consumer.common

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries
import java.time.Duration

@Configuration
class KafkaConsumerConfig {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL

        val backOff = ExponentialBackOffWithMaxRetries(ExponentialBackOffWithMaxRetries.DEFAULT_MAX_ATTEMPTS)
        backOff.initialInterval = Duration.ofSeconds(1).toMillis()
        backOff.maxInterval = Duration.ofMinutes(1).toMillis()

        val errorHandler = DefaultErrorHandler(backOff)

        errorHandler.setRetryListeners(
            { record, ex, deliveryAttempt ->
                logger.error("Failed to process record with key ${record.key()}, attempt $deliveryAttempt: ${ex.message}")
            }
        )

        factory.setCommonErrorHandler(errorHandler)
        return factory
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}