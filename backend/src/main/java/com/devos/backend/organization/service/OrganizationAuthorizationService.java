package com.devos.backend.organization.service;

import com.devos.backend.common.security.SecurityUtils;
import com.devos.backend.organization.entity.OrganizationMember;
import com.devos.backend.organization.enums.OrganizationRole;
import com.devos.backend.organization.repository.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationAuthorizationService {

    private final OrganizationMemberRepository organizationMemberRepository;

    public OrganizationMember getCurrentMembership(
            Long organizationId
    ) {

        Long userId = SecurityUtils.getCurrentUserId();

        return organizationMemberRepository
                .findByOrganizationIdAndUserId(
                        organizationId,
                        userId
                )
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "You are not a member of this organization"
                        )
                );
    }

    public void requireAdminOrOwner(
            Long organizationId
    ) {

        OrganizationMember membership =
                getCurrentMembership(organizationId);

        OrganizationRole role = membership.getRole();

        if (role != OrganizationRole.OWNER &&
                role != OrganizationRole.ADMIN) {

            throw new AccessDeniedException(
                    "You do not have permission to perform this action"
            );
        }
    }

    public void requireOwner(
            Long organizationId
    ) {

        OrganizationMember membership =
                getCurrentMembership(organizationId);

        if (membership.getRole() != OrganizationRole.OWNER) {

            throw new AccessDeniedException(
                    "Only the organization owner can perform this action"
            );
        }
    }

    public void validateRoleAssignment(
            OrganizationRole currentRole,
            OrganizationRole requestedRole
    ) {

        if (requestedRole == OrganizationRole.OWNER) {

            throw new AccessDeniedException(
                    "OWNER role cannot be assigned"
            );
        }

        if (currentRole == OrganizationRole.ADMIN &&
                requestedRole == OrganizationRole.ADMIN) {

            throw new AccessDeniedException(
                    "ADMIN cannot assign ADMIN role"
            );
        }
    }

    public void requireCanChangeRole(
            Long organizationId,
            OrganizationMember targetMember
    ) {

        OrganizationMember currentMember =
                getCurrentMembership(organizationId);

        OrganizationRole currentRole =
                currentMember.getRole();

        OrganizationRole targetCurrentRole =
                targetMember.getRole();

        if (currentMember.getUser().getId()
                .equals(targetMember.getUser().getId())) {

            throw new AccessDeniedException(
                    "You cannot change your own organization role"
            );
        }

        if (targetCurrentRole == OrganizationRole.OWNER) {

            throw new AccessDeniedException(
                    "The organization owner role cannot be changed"
            );
        }

        if (currentRole == OrganizationRole.OWNER) {
            return;
        }

        if (currentRole == OrganizationRole.ADMIN &&
                targetCurrentRole != OrganizationRole.ADMIN) {
            return;
        }

        throw new AccessDeniedException(
                "You do not have permission to change member roles"
        );
    }

    public void validateNewRole(
            OrganizationRole currentUserRole,
            OrganizationRole newRole
    ) {

        if (newRole == OrganizationRole.OWNER) {

            throw new AccessDeniedException(
                    "OWNER role cannot be assigned"
            );
        }

        if (currentUserRole == OrganizationRole.ADMIN &&
                newRole == OrganizationRole.ADMIN) {

            throw new AccessDeniedException(
                    "ADMIN cannot assign ADMIN role"
            );
        }
    }

    public void requireCanRemoveMember(
            Long organizationId,
            OrganizationMember targetMember
    ) {

        OrganizationMember currentMember =
                getCurrentMembership(organizationId);

        OrganizationRole currentRole =
                currentMember.getRole();

        OrganizationRole targetRole =
                targetMember.getRole();

        // Prevent removing yourself
        if (currentMember.getUser().getId()
                .equals(targetMember.getUser().getId())) {

            throw new AccessDeniedException(
                    "You cannot remove yourself from the organization"
            );
        }

        // Owner cannot be removed
        if (targetRole == OrganizationRole.OWNER) {

            throw new AccessDeniedException(
                    "The organization owner cannot be removed"
            );
        }

        // Owner can remove anyone except owner
        if (currentRole == OrganizationRole.OWNER) {
            return;
        }

        // Admin can remove normal members,
        // but cannot remove another admin
        if (currentRole == OrganizationRole.ADMIN &&
                targetRole != OrganizationRole.ADMIN) {

            return;
        }

        throw new AccessDeniedException(
                "You do not have permission to remove this member"
        );
    }
}