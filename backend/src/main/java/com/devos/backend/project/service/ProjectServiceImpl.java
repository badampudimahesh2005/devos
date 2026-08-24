package com.devos.backend.project.service;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.common.exception.ResourceAlreadyExistsException;
import com.devos.backend.common.exception.ResourceNotFoundException;
import com.devos.backend.common.exception.ResourceStateException;
import com.devos.backend.organization.entity.Organization;
import com.devos.backend.organization.repository.OrganizationRepository;
import com.devos.backend.organization.service.OrganizationAuthorizationService;
import com.devos.backend.project.dto.request.CreateProjectRequest;
import com.devos.backend.project.dto.request.UpdateProjectRequest;
import com.devos.backend.project.dto.response.ProjectResponse;
import com.devos.backend.project.entity.Project;
import com.devos.backend.project.enums.ProjectStatus;
import com.devos.backend.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final OrganizationRepository organizationRepository;

    private final OrganizationAuthorizationService
            organizationAuthorizationService;

    @Override
    @Transactional
    public ApiResponse<ProjectResponse> createProject(
            Long organizationId,
            CreateProjectRequest request
    ) {

        // 1. Find organization
        Organization organization = organizationRepository
                        .findById(organizationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found"
                                )
                        );

        // 2. Check whether current user
        //    is OWNER or ADMIN
        organizationAuthorizationService.requireAdminOrOwner(organizationId);

        // 3. Check duplicate project name
        if (projectRepository.existsByOrganizationIdAndName(
                        organizationId,
                        request.getName()
                )) {

            throw new ResourceAlreadyExistsException(
                    "Project with this name already exists"
            );
        }

        // 4. Check duplicate project key
        if (projectRepository.existsByOrganizationIdAndKey(
                        organizationId,
                        request.getKey()
                )) {

            throw new ResourceAlreadyExistsException(
                    "Project with this key already exists"
            );
        }

        // 5. Create project
        Project project = Project.builder()
                .organization(organization)
                .name(request.getName())
                .key(request.getKey())
                .description(request.getDescription())
                .build();

        // 6. Save
        project = projectRepository.save(project);

        // 7. Return response
        return ApiResponse.<ProjectResponse>builder()
                .success(true)
                .message("Project created successfully")
                .data(mapToProjectResponse(project))
                .timestamp(LocalDateTime.now())
                .build();
    }

    private ProjectResponse mapToProjectResponse(Project project) {

        return ProjectResponse.builder()
                .id(project.getId())
                .organizationId(project.getOrganization().getId())
                .name(project.getName())
                .key(project.getKey())
                .description(project.getDescription())
                .status(project.getStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    @Override
    public ApiResponse<ProjectResponse> getProject(
            Long organizationId,
            Long projectId
    ) {

        // 1. Verify that the current user
        //    belongs to the organization
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // 2. Find project inside the organization
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

        // 3. Return project
        return ApiResponse.<ProjectResponse>builder()
                .success(true)
                .message("Project retrieved successfully")
                .data(mapToProjectResponse(project))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public ApiResponse<List<ProjectResponse>> getProjects(
            Long organizationId
    ) {

        // 1. Verify organization membership
        organizationAuthorizationService.getCurrentMembership(organizationId);

        // 2. Find all projects belonging
        //    to this organization
        List<ProjectResponse> projects = projectRepository
                        .findByOrganizationId(organizationId)
                        .stream()
                        .map(this::mapToProjectResponse)
                        .toList();

        // 3. Return projects
        return ApiResponse.<List<ProjectResponse>>builder()
                .success(true)
                .message("Projects retrieved successfully")
                .data(projects)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<ProjectResponse> updateProject(
            Long organizationId,
            Long projectId,
            UpdateProjectRequest request
    ) {

        // 1. Only OWNER / ADMIN can update projects
        organizationAuthorizationService.requireAdminOrOwner(organizationId);

        // 2. Find project inside organization
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

        if (project.getStatus() == ProjectStatus.ARCHIVED) {

            throw new ResourceStateException(
                    "Archived projects cannot be updated"
            );
        }

        // 3. Check whether the new name
        //    conflicts with another project
        if (!project.getName().equals(request.getName())
                && projectRepository.existsByOrganizationIdAndName(
                        organizationId,
                        request.getName()
                )) {

            throw new ResourceAlreadyExistsException(
                    "Project with this name already exists"
            );
        }

        // 4. Update editable fields
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        // 5. Save
        project = projectRepository.save(project);

        // 6. Return response
        return ApiResponse.<ProjectResponse>builder()
                .success(true)
                .message("Project updated successfully")
                .data(mapToProjectResponse(project))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<ProjectResponse> archiveProject(
            Long organizationId,
            Long projectId
    ) {

        // 1. Only OWNER / ADMIN can archive projects
        organizationAuthorizationService.requireAdminOrOwner(organizationId);

        // 2. Find project inside organization
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

        // 3. Check whether project is already archived
        if (project.getStatus() == ProjectStatus.ARCHIVED) {

            throw new ResourceAlreadyExistsException(
                    "Project is already archived"
            );
        }

        // 4. Change project status
        project.setStatus(ProjectStatus.ARCHIVED);

        // 5. Save
        project = projectRepository.save(project);

        // 6. Return response
        return ApiResponse.<ProjectResponse>builder()
                .success(true)
                .message("Project archived successfully")
                .data(mapToProjectResponse(project))
                .timestamp(LocalDateTime.now())
                .build();
    }
}