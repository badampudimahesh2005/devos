package com.devos.backend.task.dto.response;

import com.devos.backend.task.enums.TaskPriority;
import com.devos.backend.task.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {

    private Long id;

    private Long projectId;

    private String projectKey;

    private String taskKey;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private Long assigneeId;

    private String assigneeName;

    private Long createdById;

    private String createdByName;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}