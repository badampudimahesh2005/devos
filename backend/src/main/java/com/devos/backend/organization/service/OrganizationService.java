package com.devos.backend.organization.service;

import com.devos.backend.auth.entity.User;
import com.devos.backend.auth.repository.UserRepository;
import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.common.exception.InvalidCredentialsException;
import com.devos.backend.common.exception.ResourceAlreadyExistsException;
import com.devos.backend.common.exception.ResourceNotFoundException;
import com.devos.backend.common.security.SecurityUtils;
import com.devos.backend.organization.dto.request.AddOrganizationMemberRequest;
import com.devos.backend.organization.dto.request.CreateOrganizationRequest;
import com.devos.backend.organization.dto.request.UpdateOrganizationMemberRoleRequest;
import com.devos.backend.organization.dto.response.OrganizationMemberResponse;
import com.devos.backend.organization.dto.response.OrganizationResponse;
import com.devos.backend.organization.entity.Organization;
import com.devos.backend.organization.entity.OrganizationMember;
import com.devos.backend.organization.enums.OrganizationRole;
import com.devos.backend.organization.repository.OrganizationMemberRepository;
import com.devos.backend.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;
    private final OrganizationAuthorizationService organizationAuthorizationService;



    @Transactional
    public ApiResponse<OrganizationResponse> createOrganization(CreateOrganizationRequest request) {

        if (organizationRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException(
                    "Organization slug already exists"
            );
        }

        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Authenticated user not found"
                        )
                );

        Organization organization = Organization.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .build();

        organization = organizationRepository.save(organization);

        OrganizationMember owner = OrganizationMember.builder()
                .organization(organization)
                .user(user)
                .role(OrganizationRole.OWNER)
                .build();

        organizationMemberRepository.save(owner);

        OrganizationResponse response =
                OrganizationResponse.builder()
                        .id(organization.getId())
                        .name(organization.getName())
                        .slug(organization.getSlug())
                        .description(organization.getDescription())
                        .logo(organization.getLogo())
                        .createdAt(organization.getCreatedAt())
                        .updatedAt(organization.getUpdatedAt())
                        .build();

        return ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Organization created successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public ApiResponse<List<OrganizationResponse>> getMyOrganizations() {

        Long userId = SecurityUtils.getCurrentUserId();

        List<OrganizationResponse> organizations =
                organizationMemberRepository
                        .findByUserId(userId)
                        .stream()
                        .map(OrganizationMember::getOrganization)
                        .map(this::mapToResponse)
                        .toList();

        return ApiResponse.<List<OrganizationResponse>>builder()
                .success(true)
                .message("Organizations retrieved successfully")
                .data(organizations)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public ApiResponse<OrganizationResponse> getOrganization(
            Long organizationId
    ) {

        Long userId = SecurityUtils.getCurrentUserId();

        OrganizationMember membership =
                organizationMemberRepository
                        .findByOrganizationIdAndUserId(
                                organizationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new AccessDeniedException(
                                        "You are not a member of this organization"
                                )
                        );

        OrganizationResponse response =
                mapToResponse(membership.getOrganization());

        return ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Organization retrieved successfully")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ApiResponse<List<OrganizationMemberResponse>>
    getOrganizationMembers(Long organizationId) {

        Long userId = SecurityUtils.getCurrentUserId();

        organizationMemberRepository
                .findByOrganizationIdAndUserId(
                        organizationId,
                        userId
                )
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "You are not a member of this organization"
                        )
                );

        List<OrganizationMemberResponse> members =
                organizationMemberRepository
                        .findByOrganizationId(organizationId)
                        .stream()
                        .map(this::mapToMemberResponse)
                        .toList();

        return ApiResponse.<List<OrganizationMemberResponse>>builder()
                .success(true)
                .message("Organization members retrieved successfully")
                .data(members)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private OrganizationResponse mapToResponse(
            Organization organization
    ) {

        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .slug(organization.getSlug())
                .description(organization.getDescription())
                .logo(organization.getLogo())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }

    private OrganizationMemberResponse mapToMemberResponse(
            OrganizationMember member
    ) {

        User user = member.getUser();

        return OrganizationMemberResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }


    //================ members ============
    @Transactional
    public ApiResponse<OrganizationMemberResponse> addMember(
            Long organizationId,
            AddOrganizationMemberRequest request
    ) {

        organizationAuthorizationService
                .requireAdminOrOwner(organizationId);

        Organization organization =
                organizationRepository.findById(organizationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Organization not found"
                                )
                        );

        User user =
                userRepository.findById(request.getUserId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        if (organizationMemberRepository
                .existsByOrganizationIdAndUserId(
                        organizationId,
                        request.getUserId()
                )) {

            throw new ResourceAlreadyExistsException(
                    "User is already a member of this organization"
            );
        }

        OrganizationMember currentMembership =
                organizationAuthorizationService
                        .getCurrentMembership(organizationId);

        organizationAuthorizationService.validateRoleAssignment(
                currentMembership.getRole(),
                request.getRole()
        );

        OrganizationMember member =
                OrganizationMember.builder()
                        .organization(organization)
                        .user(user)
                        .role(request.getRole())
                        .build();

        member = organizationMemberRepository.save(member);

        return ApiResponse.<OrganizationMemberResponse>builder()
                .success(true)
                .message("Member added successfully")
                .data(mapToMemberResponse(member))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Transactional
    public ApiResponse<OrganizationMemberResponse> updateMemberRole(
            Long organizationId,
            Long userId,
            UpdateOrganizationMemberRoleRequest request
    ) {

        OrganizationMember targetMember =
                organizationMemberRepository
                        .findByOrganizationIdAndUserId(
                                organizationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization member not found"
                                )
                        );

        OrganizationMember currentMember =
                organizationAuthorizationService
                        .getCurrentMembership(organizationId);

        organizationAuthorizationService.requireCanChangeRole(
                organizationId,
                targetMember
        );

        organizationAuthorizationService.validateNewRole(
                currentMember.getRole(),
                request.getRole()
        );

        targetMember.setRole(request.getRole());

        targetMember =
                organizationMemberRepository.save(targetMember);

        return ApiResponse.<OrganizationMemberResponse>builder()
                .success(true)
                .message("Member role updated successfully")
                .data(mapToMemberResponse(targetMember))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Transactional
    public ApiResponse<Void> removeMember(
            Long organizationId,
            Long userId
    ) {

        OrganizationMember targetMember =
                organizationMemberRepository
                        .findByOrganizationIdAndUserId(
                                organizationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization member not found"
                                )
                        );

        organizationAuthorizationService
                .requireCanRemoveMember(
                        organizationId,
                        targetMember
                );

        organizationMemberRepository.delete(targetMember);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Member removed successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

}