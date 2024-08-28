package com.example.demo.kafka.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties(ProductConfiguration::class)
@ConfigurationProperties("kafka.topics.products")
data class ProductConfiguration(
    var topic: String = ""
)