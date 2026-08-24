package com.devos.backend.project.controller;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.project.dto.request.CreateProjectRequest;
import com.devos.backend.project.dto.request.UpdateProjectRequest;
import com.devos.backend.project.dto.response.ProjectResponse;
import com.devos.backend.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @PathVariable Long organizationId,
            @Valid @RequestBody CreateProjectRequest request
    ) {

        ApiResponse<ProjectResponse> response =
                projectService.createProject(
                        organizationId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable Long organizationId,
            @PathVariable Long projectId
    ) {

        ApiResponse<ProjectResponse> response =
                projectService.getProject(
                        organizationId,
                        projectId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(
            @PathVariable Long organizationId
    ) {

        ApiResponse<List<ProjectResponse>> response =
                projectService.getProjects(
                        organizationId
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long organizationId,
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request
    ) {

        ApiResponse<ProjectResponse> response =
                projectService.updateProject(
                        organizationId,
                        projectId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/archive")
    public ResponseEntity<ApiResponse<ProjectResponse>> archiveProject(
            @PathVariable Long organizationId,
            @PathVariable Long projectId
    ) {

        ApiResponse<ProjectResponse> response =
                projectService.archiveProject(
                        organizationId,
                        projectId
                );

        return ResponseEntity.ok(response);
    }
}