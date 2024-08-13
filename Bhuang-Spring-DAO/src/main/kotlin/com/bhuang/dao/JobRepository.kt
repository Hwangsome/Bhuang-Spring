package com.bhuang.dao

import com.bhuang.model.Job
import com.bhuang.model.JobStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


//@Repository
interface JobRepository : JpaRepository<Job, Long> {
    fun findByStatus(status: JobStatus): List<Job>
}
