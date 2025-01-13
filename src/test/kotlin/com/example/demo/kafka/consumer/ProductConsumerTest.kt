package com.example.demo.kafka.consumer

import com.example.demo.TestcontainersConfiguration
import com.example.demo.kafka.consumer.service.ProductConsumerService
import com.example.demo.kafka.model.ProductReserveEvent
import com.example.demo.kafka.producer.ProductProducer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.test.context.TestPropertySource
import org.springframework.util.ResourceUtils
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest
@Testcontainers
@TestPropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application-test.properties")
@Import(TestcontainersConfiguration::class)
class ProductConsumerTest {

    @Autowired
    private lateinit var kafkaContainer: KafkaContainer

    @Value("\${kafka.topics.products.topic}")
    private lateinit var topic: String

    @MockBean
    private lateinit var productConsumerService: ProductConsumerService

    @Autowired
    private lateinit var productProducer: ProductProducer

    private lateinit var consumer: KafkaConsumer<Int, ProductReserveEvent>

    private val processedEvents = mutableListOf<ProductReserveEvent>()

    private val testEvent = ProductReserveEvent(productId = 1)

    private val latch = CountDownLatch(1)

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

        `when`(productConsumerService.processEvent(testEvent)).thenAnswer { invocation ->
            val event = invocation.getArgument<ProductReserveEvent>(0)
            processedEvents.add(event)
            latch.countDown()
        }

        consumer = KafkaConsumer(consumerProps)
        consumer.subscribe(listOf(topic))
    }

    @AfterEach
    fun tearDown() {
        consumer.close()
        processedEvents.clear()
    }

    @Test
    fun whenEventIsProduced_thenConsumerProcesses() {
        val key = 1

        productProducer.send(key, testEvent)

        val timeout = Duration.ofSeconds(10)
        val start = Instant.now()

        latch.await(10, TimeUnit.SECONDS)

        while (Duration.between(start, Instant.now()) < timeout && latch.count > 0) {
            consumer.poll(Duration.ofMillis(500))
        }

        assertEquals(1, processedEvents.size)
        assertEquals(testEvent, processedEvents.first())
    }
}