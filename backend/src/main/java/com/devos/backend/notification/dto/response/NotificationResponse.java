package com.devos.backend.notification.dto.response;

import com.devos.backend.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Long id;

    private NotificationType type;

    private String title;

    private String message;

    private String referenceType;

    private Long referenceId;

    private boolean read;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}