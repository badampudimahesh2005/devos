package com.devos.backend.task.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskCommentResponse {

    private Long id;

    private Long taskId;

    private Long userId;

    private String userName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}