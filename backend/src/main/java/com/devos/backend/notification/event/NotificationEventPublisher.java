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

    public void publishTaskStatusChanged(TaskStatusChangedEvent event) {

        eventPublisher.publishEvent(event);
    }

    public void publishTaskCommentAdded(TaskCommentAddedEvent event) {
        eventPublisher.publishEvent(event);
    }
}