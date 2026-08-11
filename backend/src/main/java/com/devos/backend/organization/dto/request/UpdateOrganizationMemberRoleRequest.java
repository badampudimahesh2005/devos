 package com.devos.backend.organization.dto.request;

import com.devos.backend.organization.enums.OrganizationRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrganizationMemberRoleRequest {

    @NotNull(message = "Organization role is required")
    private OrganizationRole role;
}