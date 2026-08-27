package com.devos.backend.task.service;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.task.dto.request.AssignTaskRequest;
import com.devos.backend.task.dto.request.CreateTaskRequest;
import com.devos.backend.task.dto.request.UpdateTaskRequest;
import com.devos.backend.task.dto.request.UpdateTaskStatusRequest;
import com.devos.backend.task.dto.response.TaskResponse;
import com.devos.backend.task.enums.TaskStatus;

import java.util.List;

public interface TaskService {

    ApiResponse<TaskResponse> createTask(
            Long organizationId,
            Long projectId,
            CreateTaskRequest request
    );

    ApiResponse<TaskResponse> getTask(
            Long organizationId,
            Long projectId,
            Long taskId
    );

    ApiResponse<List<TaskResponse>> getTasks(
            Long organizationId,
            Long projectId,
            TaskStatus status
    );

    ApiResponse<TaskResponse> updateTask(
            Long organizationId,
            Long projectId,
            Long taskId,
            UpdateTaskRequest request
    );

    ApiResponse<TaskResponse> assignTask(
            Long organizationId,
            Long projectId,
            Long taskId,
            AssignTaskRequest request
    );

    ApiResponse<TaskResponse> unassignTask(
            Long organizationId,
            Long projectId,
            Long taskId
    );

    ApiResponse<TaskResponse> updateTaskStatus(
            Long organizationId,
            Long projectId,
            Long taskId,
            UpdateTaskStatusRequest request
    );
}