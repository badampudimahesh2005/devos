package com.devos.backend.task.controller;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.task.dto.request.CreateTaskCommentRequest;
import com.devos.backend.task.dto.request.UpdateTaskCommentRequest;
import com.devos.backend.task.dto.response.TaskCommentResponse;
import com.devos.backend.task.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        "/api/organizations/{organizationId}" +
        "/projects/{projectId}" +
        "/tasks/{taskId}/comments"
)
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskCommentResponse>> createComment(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody CreateTaskCommentRequest request
    ) {

        ApiResponse<TaskCommentResponse> response =
                taskCommentService.createComment(
                        organizationId,
                        projectId,
                        taskId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<TaskCommentResponse>> updateComment(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateTaskCommentRequest request
    ) {

        ApiResponse<TaskCommentResponse> response =
                taskCommentService.updateComment(
                        organizationId,
                        projectId,
                        taskId,
                        commentId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskCommentResponse>>> getComments(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {

        ApiResponse<List<TaskCommentResponse>> response =
                taskCommentService.getComments(
                        organizationId,
                        projectId,
                        taskId
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long commentId
    ) {

        ApiResponse<Void> response = taskCommentService.deleteComment(
                        organizationId,
                        projectId,
                        taskId,
                        commentId
                );

        return ResponseEntity.ok(response);
    }
}