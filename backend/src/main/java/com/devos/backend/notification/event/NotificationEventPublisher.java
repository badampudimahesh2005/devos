package com.devos.backend.notification.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishTaskAssigned(TaskAssignedEvent event) {

        eventPublisher.publishEvent(event);
    }
}