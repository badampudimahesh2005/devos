package com.devos.backend.organization.controller;

import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.organization.dto.request.CreateOrganizationRequest;
import com.devos.backend.organization.dto.response.OrganizationMemberResponse;
import com.devos.backend.organization.dto.response.OrganizationResponse;
import com.devos.backend.organization.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(organizationService.createOrganization(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getMyOrganizations() {

        return ResponseEntity.ok(
                organizationService.getMyOrganizations()
        );
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(@PathVariable Long organizationId) {

        return ResponseEntity.ok(
                organizationService.getOrganization(organizationId)
        );
    }

    @GetMapping("/{organizationId}/members")
    public ResponseEntity<
            ApiResponse<List<OrganizationMemberResponse>>
            > getOrganizationMembers(
            @PathVariable Long organizationId
    ) {

        return ResponseEntity.ok(
                organizationService.getOrganizationMembers(
                        organizationId
                )
        );
    }
}