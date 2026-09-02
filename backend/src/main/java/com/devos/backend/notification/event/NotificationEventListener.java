package com.devos.backend.notification.event;

import com.devos.backend.notification.entity.Notification;
import com.devos.backend.notification.enums.NotificationType;
import com.devos.backend.notification.repository.NotificationRepository;
import com.devos.backend.auth.entity.User;
import com.devos.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    @EventListener
    public void handleTaskAssigned(TaskAssignedEvent event) {

        User assignee = userRepository
                        .findById(event.assigneeId())
                        .orElse(null);

        if (assignee == null) {
            return;
        }

        Notification notification = Notification.builder()
                        .user(assignee)
                        .type(NotificationType.TASK_ASSIGNED)
                        .title("Task assigned to you")
                        .message(
                                "You were assigned "
                                        + event.taskKey()
                                        + " - "
                                        + event.taskTitle()
                        )
                        .referenceType("TASK")
                        .referenceId(event.taskId())
                        .build();

        notificationRepository.save(notification);
    }


    @EventListener
    public void handleTaskStatusChanged(TaskStatusChangedEvent event) {

        User assignee = userRepository
                .findById(event.assigneeId())
                .orElse(null);

        if (assignee == null) {
            return;
        }

        // Don't notify when the user changes their own task status
        if (assignee.getId().equals(event.changedByUserId())) {
            return;
        }

        Notification notification = Notification.builder()
                .user(assignee)
                .type(NotificationType.TASK_STATUS_CHANGED)
                .title("Task status changed")
                .message(
                        event.taskKey()
                                + " - "
                                + event.taskTitle()
                                + " changed from "
                                + event.oldStatus()
                                + " to "
                                + event.newStatus()
                )
                .referenceType("TASK")
                .referenceId(event.taskId())
                .build();

        notificationRepository.save(notification);
    }

    @EventListener
    public void handleTaskCommentAdded(TaskCommentAddedEvent event) {

        User assignee = userRepository
                .findById(event.assigneeId())
                .orElse(null);

        if (assignee == null) {
            return;
        }

        // Don't notify the assignee about their own comment
        if (assignee.getId().equals(event.commentedByUserId())) {
            return;
        }

        Notification notification = Notification.builder()
                .user(assignee)
                .type(NotificationType.TASK_COMMENT_ADDED)
                .title("New comment on your task")
                .message(
                        event.commenterName()
                                + " commented on "
                                + event.taskKey()
                                + " - "
                                + event.taskTitle()
                )
                .referenceType("TASK")
                .referenceId(event.taskId())
                .build();

        notificationRepository.save(notification);
    }
}