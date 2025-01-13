package com.example.demo.kafka.producer

import com.example.demo.TestcontainersConfiguration
import com.example.demo.kafka.model.ProductReserveEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.test.context.TestPropertySource
import org.springframework.util.ResourceUtils
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@SpringBootTest
@Testcontainers
@TestPropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application-test.properties")
@Import(TestcontainersConfiguration::class)
class ProductProducerTest {
    @Autowired
    private lateinit var kafkaContainer: KafkaContainer

    @Value("\${kafka.topics.products.topic}")
    private lateinit var topic: String

    private lateinit var consumer: KafkaConsumer<Int, ProductReserveEvent>

    @Autowired
    private lateinit var productProducer: ProductProducer

    @BeforeEach
    fun setup() {
        val consumerProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "test-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to IntegerDeserializer::class.java.name,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java.name,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )

        consumer = KafkaConsumer(consumerProps)
        consumer.subscribe(listOf(topic))
    }

    @AfterEach
    fun tearDown() {
        consumer.close()
    }

    @Test
    fun whenProducerSendEvent_thenEvenSuccessfullyConsume() {
        val testEvent = ProductReserveEvent(productId = 1)
        val key = 1

        productProducer.send(key, testEvent)

        val records = mutableListOf<ConsumerRecord<Int, ProductReserveEvent>>()
        val timeout = Duration.ofSeconds(10)

        while (records.isEmpty() && timeout.toMillis() > 0) {
            consumer.poll(Duration.ofMillis(500)).forEach { records.add(it) }
        }

        val record = records.first()

        assertEquals(1, records.size)
        assertEquals(key, record.key())
        assertEquals(testEvent, record.value())
    }
}