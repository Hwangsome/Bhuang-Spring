package com.bhuang.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyApplicationContextEventListener implements ApplicationListener<ApplicationContextEvent> {

    private List<String> events = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            events.add("ContextRefreshedEvent");
        } else if (event instanceof ContextStartedEvent) {
            events.add("ContextStartedEvent");
        } else if (event instanceof ContextStoppedEvent) {
            events.add("ContextStoppedEvent");
        } else if (event instanceof ContextClosedEvent) {
            events.add("ContextClosedEvent");
        }
    }

    public List<String> getEvents() {
        return events;
    }
}
