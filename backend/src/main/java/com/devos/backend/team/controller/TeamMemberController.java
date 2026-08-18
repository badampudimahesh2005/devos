package com.devos.backend.team.controller;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.team.dto.request.AddTeamMemberRequest;
import com.devos.backend.team.dto.request.TransferTeamLeadRequest;
import com.devos.backend.team.dto.request.UpdateTeamMemberRoleRequest;
import com.devos.backend.team.dto.response.TeamMemberResponse;
import com.devos.backend.team.service.TeamMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/teams/{teamId}/members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeamMemberResponse>> addTeamMember(
            @PathVariable Long organizationId,
            @PathVariable Long teamId,
            @Valid @RequestBody AddTeamMemberRequest request
    ) {

        ApiResponse<TeamMemberResponse> response =
                teamMemberService.addTeamMember(
                        organizationId,
                        teamId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getTeamMembers(
            @PathVariable Long organizationId,
            @PathVariable Long teamId
    ) {

        ApiResponse<List<TeamMemberResponse>> response =
                teamMemberService.getTeamMembers(
                        organizationId,
                        teamId
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> updateTeamMemberRole(
            @PathVariable Long organizationId,
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateTeamMemberRoleRequest request
    ) {

        ApiResponse<TeamMemberResponse> response =
                teamMemberService.updateTeamMemberRole(
                        organizationId,
                        teamId,
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeTeamMember(
            @PathVariable Long organizationId,
            @PathVariable Long teamId,
            @PathVariable Long userId
    ) {

        ApiResponse<Void> response =
                teamMemberService.removeTeamMember(
                        organizationId,
                        teamId,
                        userId
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/lead")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> transferTeamLead(
            @PathVariable Long organizationId,
            @PathVariable Long teamId,
            @Valid @RequestBody TransferTeamLeadRequest request
    ) {

        ApiResponse<TeamMemberResponse> response =
                teamMemberService.transferTeamLead(
                        organizationId,
                        teamId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}