package com.devos.backend.notification.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationCountResponse {

    private long unreadCount;
}