package com.devos.backend.team.service;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.team.dto.request.AddTeamMemberRequest;
import com.devos.backend.team.dto.request.TransferTeamLeadRequest;
import com.devos.backend.team.dto.request.UpdateTeamMemberRoleRequest;
import com.devos.backend.team.dto.response.TeamMemberResponse;

import java.util.List;

public interface TeamMemberService {

    ApiResponse<TeamMemberResponse> addTeamMember(
            Long organizationId,
            Long teamId,
            AddTeamMemberRequest request
    );

    ApiResponse<List<TeamMemberResponse>> getTeamMembers(
            Long organizationId,
            Long teamId
    );

    ApiResponse<TeamMemberResponse> updateTeamMemberRole(
            Long organizationId,
            Long teamId,
            Long userId,
            UpdateTeamMemberRoleRequest request
    );

    ApiResponse<Void> removeTeamMember(
            Long organizationId,
            Long teamId,
            Long userId
    );

    ApiResponse<TeamMemberResponse> transferTeamLead(
            Long organizationId,
            Long teamId,
            TransferTeamLeadRequest request
    );
}