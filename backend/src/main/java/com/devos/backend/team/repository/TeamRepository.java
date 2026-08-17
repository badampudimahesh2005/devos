package com.devos.backend.team.repository;

import com.devos.backend.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByOrganizationId(Long organizationId);

    Optional<Team> findByIdAndOrganizationId(
            Long teamId,
            Long organizationId
    );

    boolean existsByOrganizationIdAndName(
            Long organizationId,
            String name
    );
}