package com.devos.backend.task.service;

import com.devos.backend.common.exception.InvalidStateTransitionException;
import com.devos.backend.common.security.SecurityUtils;
import com.devos.backend.organization.entity.OrganizationMember;
import com.devos.backend.organization.enums.OrganizationRole;
import com.devos.backend.organization.repository.OrganizationMemberRepository;
import com.devos.backend.task.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskAuthorizationService {

    private final OrganizationMemberRepository organizationMemberRepository;

    private OrganizationRole getCurrentRole(
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

        return membership.getRole();
    }

    public void requireCanCreateTask(
            Long organizationId
    ) {

        OrganizationRole role = getCurrentRole(organizationId);

        if (role == OrganizationRole.REVIEWER ||
                role == OrganizationRole.MEMBER) {

            throw new AccessDeniedException(
                    "You do not have permission to create tasks"
            );
        }
    }

    public void requireCanEditTask(
            Long organizationId
    ) {

        OrganizationRole role = getCurrentRole(organizationId);

        if (role == OrganizationRole.REVIEWER ||
                role == OrganizationRole.MEMBER) {

            throw new AccessDeniedException(
                    "You do not have permission to edit tasks"
            );
        }
    }

    public void requireCanAssignTask(
            Long organizationId
    ) {

        OrganizationRole role = getCurrentRole(organizationId);

        if (role != OrganizationRole.OWNER &&
                role != OrganizationRole.ADMIN &&
                role != OrganizationRole.PROJECT_MANAGER) {

            throw new AccessDeniedException(
                    "You do not have permission to assign tasks"
            );
        }
    }

    public void requireCanChangeStatus(
            Long organizationId
    ) {

        OrganizationRole role = getCurrentRole(organizationId);

        if (role == OrganizationRole.MEMBER) {

            throw new AccessDeniedException(
                    "You do not have permission to change task status"
            );
        }
    }


    public void validateStatusTransition(
            TaskStatus currentStatus,
            TaskStatus requestedStatus
    ) {

        if (currentStatus == requestedStatus) {
            throw new InvalidStateTransitionException(
                    "Task is already in this status"
            );
        }

        boolean allowed = switch (currentStatus) {

            case TODO ->
                    requestedStatus == TaskStatus.IN_PROGRESS;

            case IN_PROGRESS ->
                    requestedStatus == TaskStatus.TODO ||
                            requestedStatus == TaskStatus.IN_REVIEW;

            case IN_REVIEW ->
                    requestedStatus == TaskStatus.IN_PROGRESS ||
                            requestedStatus == TaskStatus.DONE;

            case DONE ->
                    requestedStatus == TaskStatus.IN_PROGRESS;
        };

        if (!allowed) {
            throw new InvalidStateTransitionException(
                    "Invalid task status transition from "
                            + currentStatus
                            + " to "
                            + requestedStatus
            );
        }
    }
}