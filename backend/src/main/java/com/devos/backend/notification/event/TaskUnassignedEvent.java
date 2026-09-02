package com.devos.backend.notification.event;

public record TaskUnassignedEvent(
        Long taskId,
        Long projectId,
        Long organizationId,
        Long previousAssigneeId,
        String taskKey,
        String taskTitle,
        Long unassignedByUserId
) {
}