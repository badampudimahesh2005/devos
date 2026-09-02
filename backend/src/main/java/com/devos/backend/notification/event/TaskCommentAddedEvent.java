package com.devos.backend.notification.event;

public record TaskCommentAddedEvent(
        Long taskId,
        Long projectId,
        Long organizationId,
        Long assigneeId,
        Long commentId,
        String taskKey,
        String taskTitle,
        Long commentedByUserId,
        String commenterName
) {
}