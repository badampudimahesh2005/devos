package com.devos.backend.notification.entity;

import com.devos.backend.notification.enums.NotificationType;
import com.devos.backend.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(
                        name = "idx_notification_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_notification_user_read",
                        columnList = "user_id, is_read"
                ),
                @Index(
                        name = "idx_notification_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private NotificationType type;

    @Column(
            nullable = false,
            length = 255
    )
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String message;

    @Column(
            name = "reference_type",
            length = 50
    )
    private String referenceType;

    @Column(
            name = "reference_id"
    )
    private Long referenceId;

    @Column(
            name = "is_read",
            nullable = false
    )
    @Builder.Default
    private boolean read = false;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}