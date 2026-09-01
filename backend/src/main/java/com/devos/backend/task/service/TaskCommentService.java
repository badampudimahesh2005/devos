package com.devos.backend.task.service;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.task.dto.request.CreateTaskCommentRequest;
import com.devos.backend.task.dto.request.UpdateTaskCommentRequest;
import com.devos.backend.task.dto.response.TaskCommentResponse;

import java.util.List;

public interface TaskCommentService {

    ApiResponse<TaskCommentResponse> createComment(
            Long organizationId,
            Long projectId,
            Long taskId,
            CreateTaskCommentRequest request
    );

    ApiResponse<TaskCommentResponse> updateComment(
            Long organizationId,
            Long projectId,
            Long taskId,
            Long commentId,
            UpdateTaskCommentRequest request
    );

    ApiResponse<List<TaskCommentResponse>> getComments(
            Long organizationId,
            Long projectId,
            Long taskId
    );

    ApiResponse<Void> deleteComment(
            Long organizationId,
            Long projectId,
            Long taskId,
            Long commentId
    );
}