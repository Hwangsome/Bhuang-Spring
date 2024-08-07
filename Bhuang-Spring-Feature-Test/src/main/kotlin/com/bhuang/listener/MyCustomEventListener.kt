package com.bhuang.listener

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ApplicationContextEvent

class MyCustomEventListener: ApplicationListener<ApplicationContextEvent> {
    override fun onApplicationEvent(event: ApplicationContextEvent) {
        println("Event Received: $event")
    }
}