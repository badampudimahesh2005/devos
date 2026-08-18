package com.devos.backend.team.mapper;

import com.devos.backend.auth.entity.User;
import com.devos.backend.organization.entity.Organization;
import com.devos.backend.team.dto.request.TeamCreateRequest;
import com.devos.backend.team.dto.response.TeamMemberResponse;
import com.devos.backend.team.dto.response.TeamResponse;
import com.devos.backend.team.dto.request.TeamUpdateRequest;
import com.devos.backend.team.entity.Team;
import com.devos.backend.team.entity.TeamMember;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public Team toEntity(TeamCreateRequest request, Organization organization) {
        return Team.builder()
                .organization(organization)
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public void updateEntity(
            Team team,
            TeamUpdateRequest request
    ) {
        team.setName(request.getName());
        team.setDescription(request.getDescription());
    }

    public TeamResponse toResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .organizationId(team.getOrganization().getId())
                .name(team.getName())
                .description(team.getDescription())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

}