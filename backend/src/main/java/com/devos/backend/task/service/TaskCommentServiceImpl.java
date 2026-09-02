package com.devos.backend.task.service;

import com.devos.backend.notification.event.NotificationEventPublisher;
import com.devos.backend.notification.event.TaskCommentAddedEvent;
import org.springframework.security.access.AccessDeniedException;
import com.devos.backend.auth.repository.UserRepository;
import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.common.exception.ResourceNotFoundException;
import com.devos.backend.common.exception.ResourceStateException;
import com.devos.backend.common.security.SecurityUtils;
import com.devos.backend.organization.repository.OrganizationMemberRepository;
import com.devos.backend.organization.service.OrganizationAuthorizationService;
import com.devos.backend.project.entity.Project;
import com.devos.backend.project.enums.ProjectStatus;
import com.devos.backend.project.repository.ProjectRepository;
import com.devos.backend.task.dto.request.CreateTaskCommentRequest;
import com.devos.backend.task.dto.request.UpdateTaskCommentRequest;
import com.devos.backend.task.dto.response.TaskCommentResponse;
import com.devos.backend.task.entity.Task;
import com.devos.backend.task.entity.TaskComment;
import com.devos.backend.task.repository.TaskCommentRepository;
import com.devos.backend.task.repository.TaskRepository;
import com.devos.backend.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCommentServiceImpl
        implements TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;

    private final TaskRepository taskRepository;

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    private final OrganizationMemberRepository organizationMemberRepository;


    private final TaskCommentAuthorizationService taskCommentAuthorizationService;

    private final OrganizationAuthorizationService organizationAuthorizationService;

    private final NotificationEventPublisher notificationEventPublisher;

    @Override
    @Transactional
    public ApiResponse<TaskCommentResponse> createComment(
            Long organizationId,
            Long projectId,
            Long taskId,
            CreateTaskCommentRequest request
    ) {

        // 1. Get authenticated user
        Long currentUserId = SecurityUtils.getCurrentUserId();

        User currentUser = userRepository
                .findById(currentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        // 2. Verify organization membership
        boolean organizationMember = organizationMemberRepository
                .existsByOrganizationIdAndUserId(
                        organizationId,
                        currentUserId
                );

        if (!organizationMember) {

            throw new ResourceNotFoundException(
                    "You are not a member of this organization"
            );
        }

        // 3. Verify project belongs to organization
        Project project = projectRepository
                .findByOrganizationIdAndId(
                        organizationId,
                        projectId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        // 4. Archived project cannot be modified
        if (project.getStatus() == ProjectStatus.ARCHIVED) {

            throw new ResourceStateException(
                    "Archived projects cannot be modified"
            );
        }

        // 5. Verify task belongs to project
        Task task = taskRepository
                .findByProjectIdAndId(
                        projectId,
                        taskId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found"
                        )
                );

        // 6. Create comment
        TaskComment comment =
                TaskComment.builder()
                        .task(task)
                        .user(currentUser)
                        .content(request.getContent())
                        .build();

        // 7. Save comment
        comment = taskCommentRepository.save(comment);

        // 8. Trigger notification
        if (task.getAssignee() != null
                && !task.getAssignee().getId().equals(currentUserId)) {

            notificationEventPublisher.publishTaskCommentAdded(
                    new TaskCommentAddedEvent(
                            task.getId(),
                            project.getId(),
                            organizationId,
                            task.getAssignee().getId(),
                            comment.getId(),
                            task.getProject().getKey() + "-" + task.getId(),
                            task.getTitle(),
                            currentUserId,
                            currentUser.getFirstName()
                                    + " "
                                    + currentUser.getLastName()
                    )
            );
        }

        // 9. Response
        return ApiResponse.<TaskCommentResponse>builder()
                .success(true)
                .message("Comment added successfully")
                .data(mapToCommentResponse(comment))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<TaskCommentResponse> updateComment(
            Long organizationId,
            Long projectId,
            Long taskId,
            Long commentId,
            UpdateTaskCommentRequest request
    ) {

        // 1. Verify organization membership
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // 2. Verify project
        Project project = projectRepository.findByOrganizationIdAndId(
                        organizationId,
                        projectId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        // 3. Verify task
        taskRepository.findByProjectIdAndId(
                        projectId,
                        taskId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found"
                        )
                );

        // 4. Find comment belonging to task
        TaskComment comment = taskCommentRepository
                        .findByIdAndTaskId(
                                commentId,
                                taskId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Comment not found"
                                )
                        );

        // 5. Authorization
        taskCommentAuthorizationService.requireCanEditComment(
                        organizationId,
                        comment
                );

        // 6. Archived projects cannot be modified
        if (project.getStatus() == ProjectStatus.ARCHIVED) {

            throw new ResourceStateException(
                    "Archived projects cannot be modified"
            );
        }

        // 7. Update
        comment.setContent(request.getContent());

        comment = taskCommentRepository.save(comment);

        return ApiResponse.<TaskCommentResponse>builder()
                .success(true)
                .message("Comment updated successfully")
                .data(mapToCommentResponse(comment))
                .timestamp(LocalDateTime.now())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TaskCommentResponse>> getComments(
            Long organizationId,
            Long projectId,
            Long taskId
    ) {

        // 1. Verify organization membership
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // 2. Verify project belongs to organization
        projectRepository.findByOrganizationIdAndId(
                        organizationId,
                        projectId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        // 3. Verify task belongs to project
        taskRepository.findByProjectIdAndId(
                        projectId,
                        taskId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found"
                        )
                );

        // 4. Get comments
        List<TaskComment> comments = taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);

        // 5. Map entities to responses
        List<TaskCommentResponse> responses =
                comments.stream()
                        .map(this::mapToCommentResponse)
                        .toList();

        return ApiResponse.<List<TaskCommentResponse>>builder()
                .success(true)
                .message("Comments retrieved successfully")
                .data(responses)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteComment(
            Long organizationId,
            Long projectId,
            Long taskId,
            Long commentId
    ) {

        // 1. Verify organization membership
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // 2. Verify project belongs to organization
        Project project = projectRepository
                        .findByOrganizationIdAndId(
                                organizationId,
                                projectId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"
                                )
                        );

        // 3. Archived project cannot be modified
        if (project.getStatus() == ProjectStatus.ARCHIVED) {

            throw new ResourceStateException(
                    "Archived projects cannot be modified"
            );
        }

        // 4. Verify task belongs to project
        taskRepository.findByProjectIdAndId(
                        projectId,
                        taskId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found"
                        )
                );

        // 5. Find comment belonging to task
        TaskComment comment = taskCommentRepository
                        .findByIdAndTaskId(
                                commentId,
                                taskId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Comment not found"
                                )
                        );

        // 6. Authorization
        taskCommentAuthorizationService.requireCanDeleteComment(
                        organizationId,
                        comment
                );

        // 7. Delete
        taskCommentRepository.delete(comment);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Comment deleted successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private TaskCommentResponse mapToCommentResponse(TaskComment comment) {

        User user = comment.getUser();

        String userName =
                user.getFirstName()
                        + " "
                        + user.getLastName();

        return TaskCommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTask().getId())
                .userId(user.getId())
                .userName(userName)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}