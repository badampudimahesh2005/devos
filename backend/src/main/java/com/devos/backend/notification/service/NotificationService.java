package com.devos.backend.notification.service;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.notification.dto.response.NotificationCountResponse;
import com.devos.backend.notification.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    ApiResponse<List<NotificationResponse>> getMyNotifications();

    ApiResponse<List<NotificationResponse>> getMyUnreadNotifications();

    ApiResponse<NotificationCountResponse> getUnreadCount();

    ApiResponse<Void> markAsRead(
            Long notificationId
    );

    ApiResponse<Void> markAllAsRead();

    ApiResponse<Void> deleteNotification(
            Long notificationId
    );
}