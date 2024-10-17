package com.example.demo.quartz.deadletter.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component


@Component
@EnableConfigurationProperties(CheckDeadLetterRetryConfiguration::class)
@ConfigurationProperties("quartz.dead-letter")
class CheckDeadLetterRetryConfiguration {
    var retryIntervals: List<String> = emptyList()
}