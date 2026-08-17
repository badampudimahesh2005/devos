package com.devos.backend.team.service;

import com.devos.backend.team.dto.request.TeamCreateRequest;
import com.devos.backend.team.dto.response.TeamResponse;
import com.devos.backend.team.dto.request.TeamUpdateRequest;

import java.util.List;

public interface TeamService {

    TeamResponse createTeam(
            Long organizationId,
            TeamCreateRequest request
    );

    List<TeamResponse> getTeamsByOrganization(
            Long organizationId
    );

    TeamResponse getTeamById(
            Long organizationId,
            Long teamId
    );

    TeamResponse updateTeam(
            Long organizationId,
            Long teamId,
            TeamUpdateRequest request
    );

    void deleteTeam(
            Long organizationId,
            Long teamId
    );
}