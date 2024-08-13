package com.bhuang.listener

import mu.KLogging
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class BotInitListerner:ApplicationListener<ContextRefreshedEvent> {
    companion object {
        private val logger = KLogging().logger()
    }
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        logger.info { "Bot is ready to serve! 🤖 :event:${event.applicationContext.applicationName}" }
    }
}