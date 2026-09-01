package com.devos.backend.notification.service;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.common.exception.ResourceNotFoundException;
import com.devos.backend.common.security.SecurityUtils;
import com.devos.backend.notification.dto.response.NotificationCountResponse;
import com.devos.backend.notification.dto.response.NotificationResponse;
import com.devos.backend.notification.entity.Notification;
import com.devos.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<NotificationResponse>> getMyNotifications() {

        Long userId = SecurityUtils.getCurrentUserId();

        List<Notification> notifications = notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                userId
                        );

        List<NotificationResponse> responses =
                notifications.stream()
                        .map(this::mapToResponse)
                        .toList();

        return ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Notifications retrieved successfully")
                .data(responses)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<NotificationResponse>> getMyUnreadNotifications() {

        Long userId = SecurityUtils.getCurrentUserId();

        List<Notification> notifications = notificationRepository
                        .findByUserIdAndReadFalseOrderByCreatedAtDesc(
                                userId
                        );

        List<NotificationResponse> responses =
                notifications.stream()
                        .map(this::mapToResponse)
                        .toList();

        return ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Unread notifications retrieved successfully")
                .data(responses)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<NotificationCountResponse> getUnreadCount() {

        Long userId = SecurityUtils.getCurrentUserId();

        long count = notificationRepository
                        .countByUserIdAndReadFalse(
                                userId
                        );

        NotificationCountResponse data = NotificationCountResponse.builder()
                        .unreadCount(count)
                        .build();

        return ApiResponse.<NotificationCountResponse>builder()
                .success(true)
                .message("Unread notification count retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> markAsRead(Long notificationId) {

        Long userId = SecurityUtils.getCurrentUserId();

        Notification notification = notificationRepository
                        .findByIdAndUserId(
                                notificationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"
                                )
                        );

        notification.setRead(true);

        notificationRepository.save(notification);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification marked as read")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> markAllAsRead() {

        Long userId = SecurityUtils.getCurrentUserId();

        List<Notification> notifications = notificationRepository
                        .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);

        notifications.forEach(
                notification -> notification.setRead(true)
        );

        notificationRepository.saveAll(notifications);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("All notifications marked as read")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteNotification(Long notificationId) {

        Long userId = SecurityUtils.getCurrentUserId();

        Notification notification = notificationRepository
                        .findByIdAndUserId(
                                notificationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"
                                )
                        );

        notificationRepository.delete(notification);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification deleted successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private NotificationResponse mapToResponse(Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}