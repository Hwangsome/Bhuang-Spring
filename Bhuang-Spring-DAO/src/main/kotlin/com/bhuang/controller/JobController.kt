package com.bhuang.controller

import com.bhuang.dao.JobRepository
import com.bhuang.model.JobStatus

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


//@RestController
//@RequestMapping("/jobs")
class JobController(private val jobRepository: JobRepository) {

    @PutMapping("/{id}/status")
    fun updateJobStatus(@PathVariable id: Long, @RequestBody request: UpdateJobStatusRequest): ResponseEntity<String> {
        val jobOptional = jobRepository.findById(id)

        return if (jobOptional.isPresent) {
            val job = jobOptional.get()
            try {
                job.status = JobStatus.valueOf(request.status.toUpperCase())
                jobRepository.save(job)
                ResponseEntity.ok("Job status updated successfully")
            } catch (e: IllegalArgumentException) {
                ResponseEntity.badRequest().body("Invalid status value")
            }
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class UpdateJobStatusRequest(
    val status: String
)
