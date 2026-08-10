package com.devos.backend.organization.dto.response;

import com.devos.backend.organization.enums.OrganizationRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrganizationMemberResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String profilePicture;

    private OrganizationRole role;

    private LocalDateTime joinedAt;
}