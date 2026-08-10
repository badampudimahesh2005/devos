package com.devos.backend.organization.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrganizationResponse {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String logo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}