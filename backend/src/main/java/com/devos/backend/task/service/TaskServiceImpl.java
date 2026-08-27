package com.devos.backend.task.service;

import com.devos.backend.auth.entity.User;
import com.devos.backend.auth.repository.UserRepository;
import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.common.exception.ResourceNotFoundException;
import com.devos.backend.common.exception.ResourceStateException;
import com.devos.backend.common.security.SecurityUtils;
import com.devos.backend.organization.entity.Organization;
import com.devos.backend.organization.repository.OrganizationMemberRepository;
import com.devos.backend.organization.repository.OrganizationRepository;
import com.devos.backend.organization.service.OrganizationAuthorizationService;
import com.devos.backend.project.entity.Project;
import com.devos.backend.project.enums.ProjectStatus;
import com.devos.backend.project.repository.ProjectRepository;
import com.devos.backend.task.dto.request.AssignTaskRequest;
import com.devos.backend.task.dto.request.CreateTaskRequest;
import com.devos.backend.task.dto.request.UpdateTaskRequest;
import com.devos.backend.task.dto.request.UpdateTaskStatusRequest;
import com.devos.backend.task.dto.response.TaskResponse;
import com.devos.backend.task.entity.Task;
import com.devos.backend.task.enums.TaskStatus;
import com.devos.backend.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final ProjectRepository projectRepository;

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    private final OrganizationAuthorizationService
            organizationAuthorizationService;

    private final OrganizationMemberRepository
            organizationMemberRepository;

    private final TaskAuthorizationService
            taskAuthorizationService;

    @Override
    @Transactional
    public ApiResponse<TaskResponse> createTask(
            Long organizationId,
            Long projectId,
            CreateTaskRequest request
    ) {

        taskAuthorizationService
                .requireCanCreateTask(organizationId);

        // 1. Verify organization exists
        Organization organization = organizationRepository
                        .findById(organizationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found"
                                )
                        );

        // 2. Verify current user belongs
        //    to the organization
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // 3. Find project inside organization
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

        // 4. Project must be active
        if (project.getStatus() == ProjectStatus.ARCHIVED) {
            throw new ResourceStateException(
                    "Cannot create task in an archived project"
            );
        }

        // 5. Get current authenticated user
        Long currentUserId = SecurityUtils.getCurrentUserId();

        User createdBy = userRepository
                        .findById(currentUserId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Current user not found"
                                )
                        );

        // 6. Resolve assignee if provided
        User assignee = null;


        if (request.getAssigneeId() != null) {

            assignee =
                    userRepository
                            .findById(request.getAssigneeId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Assignee not found"
                                    )
                            );

            boolean assigneeMember =
                    organizationMemberRepository
                            .existsByOrganizationIdAndUserId(
                                    organizationId,
                                    assignee.getId()
                            );

            if (!assigneeMember) {
                throw new ResourceNotFoundException(
                        "Assignee is not a member of this organization"
                );
            }
        }

        // 7. Create task
        Task task = Task.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .assignee(assignee)
                .createdBy(createdBy)
                .dueDate(request.getDueDate())
                .build();

        // 8. Save
        task = taskRepository.save(task);

        // 9. Return response
        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task created successfully")
                .data(mapToTaskResponse(task))
                .timestamp(LocalDateTime.now())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TaskResponse> getTask(
            Long organizationId,
            Long projectId,
            Long taskId
    ) {

        // Verify current user belongs to organization
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // Verify project belongs to organization
        projectRepository.findByOrganizationIdAndId(
                        organizationId,
                        projectId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        // Find task inside project
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

        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task retrieved successfully")
                .data(mapToTaskResponse(task))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TaskResponse>> getTasks(
            Long organizationId,
            Long projectId,
            TaskStatus status
    ) {

        // Verify organization membership
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // Verify project belongs to organization
        projectRepository.findByOrganizationIdAndId(
                        organizationId,
                        projectId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        List<Task> tasks;

        if (status != null) {

            tasks = taskRepository
                    .findByProjectIdAndStatus(
                            projectId,
                            status
                    );

        } else {

            tasks = taskRepository
                    .findByProjectId(
                            projectId
                    );
        }

        List<TaskResponse> responses =
                tasks.stream()
                        .map(this::mapToTaskResponse)
                        .toList();

        return ApiResponse.<List<TaskResponse>>builder()
                .success(true)
                .message("Tasks retrieved successfully")
                .data(responses)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<TaskResponse> updateTask(
            Long organizationId,
            Long projectId,
            Long taskId,
            UpdateTaskRequest request
    ) {

        taskAuthorizationService
                .requireCanEditTask(organizationId);

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

        // 3. Archived projects are read-only
        if (project.getStatus() == ProjectStatus.ARCHIVED) {

            throw new ResourceStateException(
                    "Archived projects cannot be updated"
            );
        }

        // 4. Find task inside project
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

        // 5. Update editable fields
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        // 6. Save
        task = taskRepository.save(task);

        // 7. Return response
        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task updated successfully")
                .data(mapToTaskResponse(task))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<TaskResponse> assignTask(
            Long organizationId,
            Long projectId,
            Long taskId,
            AssignTaskRequest request
    ) {

        taskAuthorizationService
                .requireCanAssignTask(organizationId);

        // 1. Verify current user belongs to organization
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

        // 3. Archived projects are read-only
        if (project.getStatus() == ProjectStatus.ARCHIVED) {

            throw new ResourceStateException(
                    "Archived projects cannot be modified"
            );
        }

        // 4. Find task inside project
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

        // 5. Find assignee
        User assignee = userRepository
                        .findById(request.getAssigneeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignee not found"
                                )
                        );

        // 6. Assignee must belong to same organization
        boolean assigneeMember = organizationMemberRepository
                        .existsByOrganizationIdAndUserId(
                                organizationId,
                                assignee.getId()
                        );

        if (!assigneeMember) {

            throw new ResourceNotFoundException(
                    "Assignee is not a member of this organization"
            );
        }

        // 7. Assign task
        task.setAssignee(assignee);

        task = taskRepository.save(task);

        // 8. Return response
        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task assigned successfully")
                .data(mapToTaskResponse(task))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<TaskResponse> unassignTask(
            Long organizationId,
            Long projectId,
            Long taskId
    ) {

        taskAuthorizationService
                .requireCanAssignTask(organizationId);

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

        // 3. Archived projects cannot be modified
        if (project.getStatus() == ProjectStatus.ARCHIVED) {

            throw new ResourceStateException(
                    "Archived projects cannot be modified"
            );
        }

        // 4. Find task
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

        // 5. Remove assignee
        task.setAssignee(null);

        task = taskRepository.save(task);

        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task unassigned successfully")
                .data(mapToTaskResponse(task))
                .timestamp(LocalDateTime.now())
                .build();
    }


    @Override
    @Transactional
    public ApiResponse<TaskResponse> updateTaskStatus(
            Long organizationId,
            Long projectId,
            Long taskId,
            UpdateTaskStatusRequest request
    ) {

        taskAuthorizationService.requireCanChangeStatus(organizationId);

        // 1. Verify organization membership
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // 2. Verify project
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

        // 4. Find task
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

        //before saving validate the task flow
        taskAuthorizationService
                .validateStatusTransition(
                        task.getStatus(),
                        request.getStatus()
                );

        // 5. Update status
        task.setStatus(request.getStatus());

        task = taskRepository.save(task);

        return ApiResponse.<TaskResponse>builder()
                .success(true)
                .message("Task status updated successfully")
                .data(mapToTaskResponse(task))
                .timestamp(LocalDateTime.now())
                .build();
    }


    private TaskResponse mapToTaskResponse(
            Task task
    ) {

        return TaskResponse.builder()
                .id(task.getId())
                .projectId(
                        task.getProject().getId()
                )
                .projectKey(
                        task.getProject().getKey()
                )
                .taskKey(
                        task.getProject().getKey()
                                + "-"
                                + task.getId()
                )
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assigneeId(
                        task.getAssignee() != null
                                ? task.getAssignee().getId()
                                : null
                )
                .assigneeName(
                        task.getAssignee() != null
                                ? task.getAssignee().getFirstName()
                                  + " "
                                  + task.getAssignee().getLastName()
                                : null
                )
                .createdById(
                        task.getCreatedBy().getId()
                )
                .createdByName(
                        task.getCreatedBy().getFirstName()
                                + " "
                                + task.getCreatedBy().getLastName()
                )
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}