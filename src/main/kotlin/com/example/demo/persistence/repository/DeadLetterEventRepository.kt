package com.example.demo.persistence.repository

import com.example.demo.persistence.model.DeadLetterEventModel
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface DeadLetterEventRepository : CrudRepository<DeadLetterEventModel, Int> {
    @Modifying
    @Transactional
    @Query("INSERT INTO dead_letter_events VALUES (:#{#event.id}, :#{#event.payload}, :#{#event.createDate}, :#{#event.updateDate}, :#{#event.currentAttempt})")
    fun insert(@Param("event") event: DeadLetterEventModel)
}