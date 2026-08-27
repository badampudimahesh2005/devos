package com.devos.backend.task.controller;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.task.dto.request.AssignTaskRequest;
import com.devos.backend.task.dto.request.CreateTaskRequest;
import com.devos.backend.task.dto.request.UpdateTaskRequest;
import com.devos.backend.task.dto.request.UpdateTaskStatusRequest;
import com.devos.backend.task.dto.response.TaskResponse;
import com.devos.backend.task.enums.TaskStatus;
import com.devos.backend.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request
    ) {

        ApiResponse<TaskResponse> response =
                taskService.createTask(
                        organizationId,
                        projectId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {

        ApiResponse<TaskResponse> response =
                taskService.getTask(
                        organizationId,
                        projectId,
                        taskId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status
    ) {

        ApiResponse<List<TaskResponse>> response =
                taskService.getTasks(
                        organizationId,
                        projectId,
                        status
                );

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {

        ApiResponse<TaskResponse> response =
                taskService.updateTask(
                        organizationId,
                        projectId,
                        taskId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request
    ) {

        ApiResponse<TaskResponse> response =
                taskService.assignTask(
                        organizationId,
                        projectId,
                        taskId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{taskId}/unassign")
    public ResponseEntity<ApiResponse<TaskResponse>> unassignTask(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {

        ApiResponse<TaskResponse> response =
                taskService.unassignTask(
                        organizationId,
                        projectId,
                        taskId
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ) {

        ApiResponse<TaskResponse> response =
                taskService.updateTaskStatus(
                        organizationId,
                        projectId,
                        taskId,
                        request
                );

        return ResponseEntity.ok(response);
    }

}