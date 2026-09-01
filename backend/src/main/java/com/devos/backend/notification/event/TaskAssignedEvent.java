package com.devos.backend.notification.event;

public record TaskAssignedEvent(
        Long taskId,
        Long projectId,
        Long organizationId,
        Long assigneeId,
        String taskKey,
        String taskTitle,
        Long assignedByUserId
) {
}