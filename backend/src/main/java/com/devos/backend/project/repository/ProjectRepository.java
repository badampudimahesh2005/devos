package com.devos.backend.project.repository;

import com.devos.backend.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository
        extends JpaRepository<Project, Long> {

    List<Project> findByOrganizationId(
            Long organizationId
    );

    Optional<Project> findByOrganizationIdAndId(
            Long organizationId,
            Long projectId
    );

    boolean existsByOrganizationIdAndName(
            Long organizationId,
            String name
    );

    boolean existsByOrganizationIdAndKey(
            Long organizationId,
            String key
    );
}