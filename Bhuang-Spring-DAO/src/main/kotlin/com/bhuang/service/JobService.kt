package com.bhuang.service

import com.bhuang.BhuangSpringDaoApplication
import com.bhuang.dao.JobRepository
import com.bhuang.model.JobStatus
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.context.ApplicationContext
import org.springframework.boot.SpringApplication
import java.sql.Timestamp
import java.time.LocalDateTime

//@Service
class JobService(
    private val jobRepository: JobRepository,
    private val context: ApplicationContext
) {
    companion object {
        private val logger = KLogging().logger()
    }

    @Value("\${job.timeout.minutes}")
    private lateinit var jobTimeoutMinutes:String


    //@Scheduled(fixedRate = 60000)  // 每分钟执行一次
    fun checkAndRestart() {
        logger.info { "Checking for overdue jobs"}
        val timeoutThreshold = Timestamp.valueOf(LocalDateTime.now().minusMinutes(jobTimeoutMinutes.toLong()))

        val processingJobs = jobRepository.findByStatus(JobStatus.PROCESSING)

        if (processingJobs.isNotEmpty()) {
            logger.info { "Found ${processingJobs.size} overdue jobs" }
            restartApplication()
        } else {
            logger.info { "NOT Found ${processingJobs.size} overdue jobs" }
        }
    }

    private fun restartApplication() {
        val args = context.getBean(org.springframework.boot.ApplicationArguments::class.java).sourceArgs
        logger.info { "Restarting application" }
        Thread {
            try {
                logger.info { "Waiting for 60 seconds before restarting"}
                Thread.sleep(60000)  // 延迟 5 秒再重启
                SpringApplication.exit(context, { 0 })
                SpringApplication.run(BhuangSpringDaoApplication::class.java, *args)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.apply {
            isDaemon = false
            start()
        }
    }
}
