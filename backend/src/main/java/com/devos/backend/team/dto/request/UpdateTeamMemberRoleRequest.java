package com.devos.backend.team.dto.request;

import com.devos.backend.team.enums.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTeamMemberRoleRequest {

    @NotNull(message = "Team role is required")
    private TeamRole role;
}