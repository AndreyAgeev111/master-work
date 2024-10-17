package com.example.demo.quartz.deadletter.util

import org.springframework.stereotype.Service
import java.time.Duration

interface TimeService {
    fun parseInterval(interval: String): Duration
}

@Service("timeService")
class TimeServiceImpl : TimeService {
    override fun parseInterval(interval: String): Duration {
        val timeUnit = interval.last()
        val amount = interval.dropLast(1).toLong()

        return when (timeUnit) {
            's' -> Duration.ofSeconds(amount)
            'm' -> Duration.ofMinutes(amount)
            'h' -> Duration.ofHours(amount)
            'd' -> Duration.ofDays(amount)
            else -> throw IllegalArgumentException("Unknown time unit: $timeUnit")
        }
    }
}