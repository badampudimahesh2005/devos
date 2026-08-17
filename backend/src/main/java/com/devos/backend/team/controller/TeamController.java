package com.devos.backend.team.controller;

import com.devos.backend.team.dto.request.TeamCreateRequest;
import com.devos.backend.team.dto.response.TeamResponse;
import com.devos.backend.team.dto.request.TeamUpdateRequest;
import com.devos.backend.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @PathVariable Long organizationId,
            @Valid @RequestBody TeamCreateRequest request
    ) {

        TeamResponse response = teamService.createTeam(
                organizationId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeams(
            @PathVariable Long organizationId
    ) {

        List<TeamResponse> response =
                teamService.getTeamsByOrganization(
                        organizationId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeam(
            @PathVariable Long organizationId,
            @PathVariable Long teamId
    ) {

        TeamResponse response =
                teamService.getTeamById(
                        organizationId,
                        teamId
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long organizationId,
            @PathVariable Long teamId,
            @Valid @RequestBody TeamUpdateRequest request
    ) {

        TeamResponse response =
                teamService.updateTeam(
                        organizationId,
                        teamId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable Long organizationId,
            @PathVariable Long teamId
    ) {

        teamService.deleteTeam(
                organizationId,
                teamId
        );

        return ResponseEntity.noContent().build();
    }
}