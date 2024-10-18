package com.example.demo.service

import com.example.demo.persistence.model.DeadLetterEventModel
import com.example.demo.persistence.repository.DeadLetterEventRepository
import com.example.demo.service.exception.EventNotFoundException
import com.example.demo.service.model.DeadLetterEvent
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicLong
import kotlin.jvm.optionals.getOrElse

interface DeadLetterEventService {
    fun getEventById(id: Int): DeadLetterEvent
    fun listEvents(): List<DeadLetterEvent>
    fun deleteById(id: Int)
    fun upsertEvent(deadLetterEvent: DeadLetterEvent)
    fun countEvents(): Long
}

@Service("deadLetterEventService")
class DeadLetterEventsServiceImpl(val db: DeadLetterEventRepository, meterRegistry: MeterRegistry) :
    DeadLetterEventService {
    override fun getEventById(id: Int): DeadLetterEvent = getEvent(id)

    override fun listEvents(): List<DeadLetterEvent> = db.findAll().toList().map { DeadLetterEvent(it) }

    override fun deleteById(id: Int) {
        getEventById(id).let {
            db.deleteById(it.id)
            eventCount.getAndDecrement()
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun upsertEvent(deadLetterEvent: DeadLetterEvent) {
        db.findById(deadLetterEvent.id).ifPresentOrElse(
            { db.save(DeadLetterEventModel(deadLetterEvent)) },
            { db.insert(DeadLetterEventModel(deadLetterEvent)).also { eventCount.getAndIncrement() } }
        )
    }

    override fun countEvents() = db.count()

    private fun getEvent(id: Int) = db.findById(id).getOrElse {
        logger.error("Event with id $id was not found")
        throw EventNotFoundException(id)
    }.let { DeadLetterEvent(it) }

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val eventCount = AtomicLong(0)

    init {
        Gauge.builder("dead_letter_event_count") { eventCount.get() }
            .register(meterRegistry)
    }
}
