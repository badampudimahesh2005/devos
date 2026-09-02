package com.devos.backend.notification.event;

public record TaskStatusChangedEvent(
        Long taskId,
        Long projectId,
        Long organizationId,
        Long assigneeId,
        String taskKey,
        String taskTitle,
        String oldStatus,
        String newStatus,
        Long changedByUserId
) {
}