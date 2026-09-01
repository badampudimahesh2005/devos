package com.devos.backend.notification.controller;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.notification.dto.response.NotificationCountResponse;
import com.devos.backend.notification.dto.response.NotificationResponse;
import com.devos.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Get all notifications of current user
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    // Get unread notifications of current user
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getMyUnreadNotifications());
    }

    // Get unread notification count
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<NotificationCountResponse>> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }

    // Mark one notification as read
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId
    ) {
        return ResponseEntity.ok(
                notificationService.markAsRead(notificationId)
        );
    }

    // Mark all notifications as read
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        return ResponseEntity.ok(
                notificationService.markAllAsRead()
        );
    }

    // Delete one notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long notificationId
    ) {
        return ResponseEntity.ok(
                notificationService.deleteNotification(notificationId)
        );
    }
}