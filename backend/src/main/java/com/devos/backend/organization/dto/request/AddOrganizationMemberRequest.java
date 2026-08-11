package com.devos.backend.organization.dto.request;

import com.devos.backend.organization.enums.OrganizationRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddOrganizationMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Organization role is required")
    private OrganizationRole role;
}