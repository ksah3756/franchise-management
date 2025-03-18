package com.goorm.friendchise.global.auth.infrastructure;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FakeApplicationEventPublisher implements ApplicationEventPublisher {
    private final Queue<Object> publishedEvents = new ConcurrentLinkedQueue<>();

    @Override
    public void publishEvent(ApplicationEvent event) {
        publishedEvents.add(event);
    }

    @Override
    public void publishEvent(Object event) {
        publishedEvents.add(event);
    }

    public Queue<Object> getPublishedEvents() {
        return publishedEvents;
    }
}
