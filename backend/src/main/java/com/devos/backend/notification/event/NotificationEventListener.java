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
}