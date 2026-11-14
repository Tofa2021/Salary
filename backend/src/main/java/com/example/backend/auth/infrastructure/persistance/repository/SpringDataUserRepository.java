package com.example.backend.auth.infrastructure.persistance.repository;

import com.example.backend.auth.domain.model.UserRole;
import com.example.backend.auth.infrastructure.persistance.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByRole(UserRole role);
}
