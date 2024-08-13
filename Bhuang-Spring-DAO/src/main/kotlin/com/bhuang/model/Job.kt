package com.bhuang.model

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "job")
data class Job(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "job_name")
    var jobName: String? =null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: JobStatus = JobStatus.PROCESSING,

    @Column(name = "created_at")
    var createdAt: Timestamp? = null,

    @Column(name = "updated_at")
    var updatedAt: Timestamp? = null
)

enum class JobStatus {
    CANCELLED,
    PROCESSING,
    COMPLETED
}
