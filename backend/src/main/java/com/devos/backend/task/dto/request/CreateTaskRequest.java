package com.devos.backend.task.dto.request;

import com.devos.backend.task.enums.TaskPriority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(
            min = 2,
            max = 200,
            message = "Task title must be between 2 and 200 characters"
    )
    private String title;

    @Size(
            max = 5000,
            message = "Task description cannot exceed 5000 characters"
    )
    private String description;

    private TaskPriority priority;

    private Long assigneeId;

    @FutureOrPresent(
            message = "Due date cannot be in the past"
    )
    private LocalDate dueDate;
}