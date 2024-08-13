package com.bhuang.processor

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BotReInitProcessor @Autowired constructor(
    private val eventPublisher: ApplicationEventPublisher,
    private val applicationContext: ApplicationContext
) {

    companion object {
        private val logger = KLogging().logger()
    }

    @Scheduled(cron = "0 */1 * * * ?")
    fun reInitBot() {
        logger.info { "Reinitializing bot" }
        eventPublisher.publishEvent(ContextRefreshedEvent(applicationContext))
    }
}