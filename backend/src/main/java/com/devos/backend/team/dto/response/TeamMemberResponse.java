package com.devos.backend.team.dto.response;

import com.devos.backend.team.enums.TeamRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TeamMemberResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String profilePicture;

    private TeamRole role;

    private LocalDateTime joinedAt;
}