package com.devos.backend.task.service;

import com.devos.backend.common.security.SecurityUtils;
import com.devos.backend.organization.entity.OrganizationMember;
import com.devos.backend.organization.enums.OrganizationRole;
import com.devos.backend.organization.repository.OrganizationMemberRepository;
import com.devos.backend.task.entity.TaskComment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskCommentAuthorizationService {

    private final OrganizationMemberRepository
            organizationMemberRepository;

    public void requireCanEditComment(
            Long organizationId,
            TaskComment comment
    ) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        // Comment owner can edit
        if (comment.getUser().getId().equals(currentUserId)) {
            return;
        }

        OrganizationMember membership = organizationMemberRepository
                        .findByOrganizationIdAndUserId(
                                organizationId,
                                currentUserId
                        )
                        .orElseThrow(() ->
                                new AccessDeniedException(
                                        "You are not a member of this organization"
                                )
                        );

        OrganizationRole role = membership.getRole();

        // Management roles can edit others' comments
        if (role == OrganizationRole.OWNER ||
                role == OrganizationRole.ADMIN ||
                role == OrganizationRole.PROJECT_MANAGER) {

            return;
        }

        throw new AccessDeniedException(
                "You do not have permission to edit this comment"
        );
    }

    public void requireCanDeleteComment(
            Long organizationId,
            TaskComment comment
    ) {

        Long currentUserId =
                SecurityUtils.getCurrentUserId();

        // Comment owner can delete
        if (comment.getUser().getId()
                .equals(currentUserId)) {

            return;
        }

        OrganizationMember membership =
                organizationMemberRepository
                        .findByOrganizationIdAndUserId(
                                organizationId,
                                currentUserId
                        )
                        .orElseThrow(() ->
                                new AccessDeniedException(
                                        "You are not a member of this organization"
                                )
                        );

        OrganizationRole role =
                membership.getRole();

        // Management roles can delete other users' comments
        if (role == OrganizationRole.OWNER ||
                role == OrganizationRole.ADMIN ||
                role == OrganizationRole.PROJECT_MANAGER) {

            return;
        }

        throw new AccessDeniedException(
                "You do not have permission to delete this comment"
        );
    }
}