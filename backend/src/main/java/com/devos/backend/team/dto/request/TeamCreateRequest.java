package com.devos.backend.team.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCreateRequest {

    @NotBlank(message = "Team name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Team name must be between 2 and 100 characters"
    )
    private String name;

    @Size(
        max = 500,
        message = "Description cannot exceed 500 characters"
    )
    private String description;
}