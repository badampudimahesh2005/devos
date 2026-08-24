package com.devos.backend.project.dto.response;

import com.devos.backend.project.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectResponse {

    private Long id;

    private Long organizationId;

    private String name;

    private String key;

    private String description;

    private ProjectStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}