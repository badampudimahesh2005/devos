package com.devos.backend.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Project name must be between 2 and 150 characters"
    )
    private String name;

    @NotBlank(message = "Project key is required")
    @Size(
            min = 2,
            max = 20,
            message = "Project key must be between 2 and 20 characters"
    )
    private String key;

    @Size(
            max = 2000,
            message = "Description cannot exceed 2000 characters"
    )
    private String description;
}