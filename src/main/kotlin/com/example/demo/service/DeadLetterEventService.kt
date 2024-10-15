package com.example.demo.service

import com.example.demo.persistence.model.DeadLetterEventModel
import com.example.demo.persistence.repository.DeadLetterEventRepository
import com.example.demo.service.exception.EventNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

interface DeadLetterEventsService {
    fun getEventById(id: Int): DeadLetterEventModel
    fun upsertEvent(deadLetterEvent: DeadLetterEventModel)
}

@Service("deadLetterEventService")
class DeadLetterEventsServiceImpl(val db: DeadLetterEventRepository) :
    DeadLetterEventsService {
    override fun getEventById(id: Int): DeadLetterEventModel = db.findById(id).getOrElse {
        logger.error("Event with id $id was not found")
        throw EventNotFoundException(id)
    }

    @Transactional
    override fun upsertEvent(deadLetterEvent: DeadLetterEventModel) {
        db.findById(deadLetterEvent.id).ifPresentOrElse(
            { db.save(deadLetterEvent) },
            { db.insert(deadLetterEvent) }
        )
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
}