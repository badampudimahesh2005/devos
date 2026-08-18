package com.devos.backend.team.repository;

import com.devos.backend.team.entity.TeamMember;
import com.devos.backend.team.enums.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository
        extends JpaRepository<TeamMember, Long> {

    boolean existsByTeamIdAndUserId(
            Long teamId,
            Long userId
    );

    List<TeamMember> findByTeamId(
            Long teamId
    );

    Optional<TeamMember> findByTeamIdAndUserId(
            Long teamId,
            Long userId
    );

    List<TeamMember> findByUserId(
            Long userId
    );
    Optional<TeamMember> findByTeamIdAndRole(
            Long teamId,
            TeamRole role
    );
}