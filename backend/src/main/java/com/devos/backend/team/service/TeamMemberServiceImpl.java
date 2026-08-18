package com.devos.backend.team.service;

import com.devos.backend.auth.entity.User;
import com.devos.backend.auth.repository.UserRepository;
import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.common.exception.ResourceAlreadyExistsException;
import com.devos.backend.common.exception.ResourceNotFoundException;
import com.devos.backend.organization.repository.OrganizationMemberRepository;
import com.devos.backend.organization.service.OrganizationAuthorizationService;
import com.devos.backend.team.dto.request.AddTeamMemberRequest;
import com.devos.backend.team.dto.request.TransferTeamLeadRequest;
import com.devos.backend.team.dto.request.UpdateTeamMemberRoleRequest;
import com.devos.backend.team.dto.response.TeamMemberResponse;
import com.devos.backend.team.entity.Team;
import com.devos.backend.team.entity.TeamMember;
import com.devos.backend.team.enums.TeamRole;
import com.devos.backend.team.repository.TeamMemberRepository;
import com.devos.backend.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final UserRepository userRepository;

    private final OrganizationMemberRepository organizationMemberRepository;

    private final OrganizationAuthorizationService
            organizationAuthorizationService;

    @Override
    @Transactional
    public ApiResponse<TeamMemberResponse> addTeamMember(
            Long organizationId,
            Long teamId,
            AddTeamMemberRequest request
    ) {

        Team team = teamRepository
                .findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        if (!team.getOrganization()
                .getId()
                .equals(organizationId)) {

            throw new ResourceNotFoundException(
                    "Team not found in this organization"
            );
        }

        organizationAuthorizationService
                .requireAdminOrOwner(organizationId);

        if (request.getRole() == TeamRole.TEAM_LEAD) {
            throw new AccessDeniedException(
                    "TEAM_LEAD can only be assigned through team lead management"
            );
        }

        User user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        boolean organizationMember =
                organizationMemberRepository
                        .existsByOrganizationIdAndUserId(
                                organizationId,
                                user.getId()
                        );

        if (!organizationMember) {

            throw new ResourceNotFoundException(
                    "User is not a member of this organization"
            );
        }

        if (teamMemberRepository
                .existsByTeamIdAndUserId(
                        teamId,
                        user.getId()
                )) {

            throw new ResourceAlreadyExistsException(
                    "User is already a member of this team"
            );
        }

        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .role(request.getRole())
                .build();

        teamMember = teamMemberRepository
                .save(teamMember);

        return ApiResponse.<TeamMemberResponse>builder()
                .success(true)
                .message("Team member added successfully")
                .data(mapToTeamMemberResponse(teamMember))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public ApiResponse<List<TeamMemberResponse>> getTeamMembers(
            Long organizationId,
            Long teamId
    ) {

        Team team = teamRepository
                .findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        if (!team.getOrganization()
                .getId()
                .equals(organizationId)) {

            throw new ResourceNotFoundException(
                    "Team not found in this organization"
            );
        }

        organizationAuthorizationService
                .getCurrentMembership(organizationId);

        List<TeamMemberResponse> members =
                teamMemberRepository
                        .findByTeamId(teamId)
                        .stream()
                        .map(this::mapToTeamMemberResponse)
                        .toList();

        return ApiResponse.<List<TeamMemberResponse>>builder()
                .success(true)
                .message("Team members retrieved successfully")
                .data(members)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<TeamMemberResponse> updateTeamMemberRole(
            Long organizationId,
            Long teamId,
            Long userId,
            UpdateTeamMemberRoleRequest request
    ) {

        Team team = teamRepository
                .findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        if (!team.getOrganization()
                .getId()
                .equals(organizationId)) {

            throw new ResourceNotFoundException(
                    "Team not found in this organization"
            );
        }

        organizationAuthorizationService
                .requireAdminOrOwner(organizationId);

        TeamMember teamMember =
                teamMemberRepository
                        .findByTeamIdAndUserId(
                                teamId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team member not found"
                                )
                        );

        if (teamMember.getRole() == TeamRole.TEAM_LEAD) {

            throw new AccessDeniedException(
                    "The team lead role cannot be changed"
            );
        }

        if (request.getRole() == TeamRole.TEAM_LEAD) {

            throw new AccessDeniedException(
                    "TEAM_LEAD role cannot be assigned through this endpoint"
            );
        }

        teamMember.setRole(request.getRole());

        teamMember = teamMemberRepository
                .save(teamMember);

        return ApiResponse.<TeamMemberResponse>builder()
                .success(true)
                .message("Team member role updated successfully")
                .data(mapToTeamMemberResponse(teamMember))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> removeTeamMember(
            Long organizationId,
            Long teamId,
            Long userId
    ) {

        Team team = teamRepository
                .findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        if (!team.getOrganization()
                .getId()
                .equals(organizationId)) {

            throw new ResourceNotFoundException(
                    "Team not found in this organization"
            );
        }

        organizationAuthorizationService
                .requireAdminOrOwner(organizationId);

        TeamMember teamMember =
                teamMemberRepository
                        .findByTeamIdAndUserId(
                                teamId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Team member not found"
                                )
                        );

        if (teamMember.getRole() == TeamRole.TEAM_LEAD) {

            throw new AccessDeniedException(
                    "The team lead cannot be removed through this endpoint"
            );
        }

        teamMemberRepository.delete(teamMember);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Team member removed successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<TeamMemberResponse> transferTeamLead(
            Long organizationId,
            Long teamId,
            TransferTeamLeadRequest request
    ) {

        // 1. Find the team
        Team team = teamRepository
                .findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found"
                        )
                );

        // 2. Verify team belongs to organization
        if (!team.getOrganization()
                .getId()
                .equals(organizationId)) {

            throw new ResourceNotFoundException(
                    "Team not found in this organization"
            );
        }

        // 3. Only OWNER / ADMIN can transfer leadership
        organizationAuthorizationService
                .requireAdminOrOwner(organizationId);

        // 4. Find target team member
        TeamMember newLead =
                teamMemberRepository
                        .findByTeamIdAndUserId(
                                teamId,
                                request.getUserId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User is not a member of this team"
                                )
                        );

        // 5. If target is already the team lead,
        //    there is nothing to change
        if (newLead.getRole() == TeamRole.TEAM_LEAD) {

            return ApiResponse.<TeamMemberResponse>builder()
                    .success(true)
                    .message("User is already the team lead")
                    .data(mapToTeamMemberResponse(newLead))
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // 6. Find current team lead
        TeamMember currentLead =
                teamMemberRepository
                        .findByTeamIdAndRole(
                                teamId,
                                TeamRole.TEAM_LEAD
                        )
                        .orElse(null);

        // 7. Demote current lead
        if (currentLead != null) {
            currentLead.setRole(TeamRole.MEMBER);
            teamMemberRepository.save(currentLead);
        }

        // 8. Promote new lead
        newLead.setRole(TeamRole.TEAM_LEAD);
        teamMemberRepository.save(newLead);

        return ApiResponse.<TeamMemberResponse>builder()
                .success(true)
                .message("Team lead transferred successfully")
                .data(mapToTeamMemberResponse(newLead))
                .timestamp(LocalDateTime.now())
                .build();
    }

    private TeamMemberResponse mapToTeamMemberResponse(
            TeamMember teamMember
    ) {

        User user = teamMember.getUser();

        return TeamMemberResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .role(teamMember.getRole())
                .joinedAt(teamMember.getJoinedAt())
                .build();
    }
}