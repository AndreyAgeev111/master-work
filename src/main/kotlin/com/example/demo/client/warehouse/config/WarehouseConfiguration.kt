package com.example.demo.client.warehouse.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties(WarehouseConfiguration::class)
@ConfigurationProperties("http.client.warehouse")
class WarehouseConfiguration {
    var baseUrl: String = ""
}