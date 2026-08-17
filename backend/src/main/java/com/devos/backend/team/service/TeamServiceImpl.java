package com.devos.backend.team.service;

import com.devos.backend.organization.entity.Organization;
import com.devos.backend.organization.repository.OrganizationRepository;
import com.devos.backend.organization.service.OrganizationAuthorizationService;
import com.devos.backend.team.dto.request.TeamCreateRequest;
import com.devos.backend.team.dto.response.TeamResponse;
import com.devos.backend.team.dto.request.TeamUpdateRequest;
import com.devos.backend.team.entity.Team;
import com.devos.backend.team.exception.DuplicateTeamNameException;
import com.devos.backend.team.exception.TeamNotFoundException;
import com.devos.backend.team.mapper.TeamMapper;
import com.devos.backend.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationAuthorizationService authorizationService;
    private final TeamMapper teamMapper;

    @Override
    public TeamResponse createTeam(Long organizationId, TeamCreateRequest request) {

        // 1. Verify current user can manage the organization
        authorizationService.requireAdminOrOwner(
                organizationId
        );

        // 2. Verify organization exists
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Organization not found"
                        )
                );

        // 3. Check duplicate team name
        if (teamRepository.existsByOrganizationIdAndName(organizationId, request.getName())) {
            throw new DuplicateTeamNameException(request.getName());
        }

        // 4. Convert request → entity
        Team team = teamMapper.toEntity(
                request,
                organization
        );

        // 5. Persist
        Team savedTeam = teamRepository.save(team);

        // 6. Convert entity → response
        return teamMapper.toResponse(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsByOrganization(Long organizationId) {

        // Verify organization exists
        if (!organizationRepository.existsById(
                organizationId
        )) {
            throw new RuntimeException(
                    "Organization not found"
            );
        }

        // Read access is currently allowed
        // for organization members.
        authorizationService.getCurrentMembership(
                organizationId
        );

        return teamRepository
                .findAllByOrganizationId(organizationId)
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long organizationId, Long teamId) {

        // Verify organization membership
        authorizationService.getCurrentMembership(
                organizationId
        );

        Team team = teamRepository
                .findByIdAndOrganizationId(
                        teamId,
                        organizationId
                )
                .orElseThrow(() ->
                        new TeamNotFoundException(teamId)
                );

        return teamMapper.toResponse(team);
    }

    @Override
    public TeamResponse updateTeam(
            Long organizationId,
            Long teamId,
            TeamUpdateRequest request
    ) {

        // Only OWNER / ADMIN can update teams
        authorizationService.requireAdminOrOwner(
                organizationId
        );

        Team team = teamRepository
                .findByIdAndOrganizationId(
                        teamId,
                        organizationId
                )
                .orElseThrow(() ->
                        new TeamNotFoundException(teamId)
                );

        boolean nameChanged =
                !team.getName().equals(request.getName());

        if (nameChanged &&
                teamRepository.existsByOrganizationIdAndName(
                        organizationId,
                        request.getName()
                )) {

            throw new DuplicateTeamNameException(request.getName());
        }

        teamMapper.updateEntity(
                team,
                request
        );

        return teamMapper.toResponse(team);
    }

    @Override
    public void deleteTeam(Long organizationId, Long teamId) {

        // Only OWNER / ADMIN can delete teams
        authorizationService.requireAdminOrOwner(
                organizationId
        );

        Team team = teamRepository
                .findByIdAndOrganizationId(
                        teamId,
                        organizationId
                )
                .orElseThrow(() ->
                        new TeamNotFoundException(teamId)
                );

        teamRepository.delete(team);
    }
}