package com.devos.backend.project.service;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.project.dto.request.CreateProjectRequest;
import com.devos.backend.project.dto.request.UpdateProjectRequest;
import com.devos.backend.project.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ApiResponse<ProjectResponse> createProject(
            Long organizationId,
            CreateProjectRequest request
    );

    ApiResponse<ProjectResponse> getProject(
            Long organizationId,
            Long projectId
    );

    ApiResponse<List<ProjectResponse>> getProjects(
            Long organizationId
    );

    ApiResponse<ProjectResponse> updateProject(
            Long organizationId,
            Long projectId,
            UpdateProjectRequest request
    );

    ApiResponse<ProjectResponse> archiveProject(
            Long organizationId,
            Long projectId
    );
}