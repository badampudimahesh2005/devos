package com.devos.backend.organization.repository;

import com.devos.backend.organization.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository
        extends JpaRepository<OrganizationMember, Long> {

    List<OrganizationMember> findByOrganizationId(Long organizationId);

    List<OrganizationMember> findByUserId(Long userId);

    Optional<OrganizationMember> findByOrganizationIdAndUserId(
            Long organizationId,
            Long userId
    );

    boolean existsByOrganizationIdAndUserId(
            Long organizationId,
            Long userId
    );
}