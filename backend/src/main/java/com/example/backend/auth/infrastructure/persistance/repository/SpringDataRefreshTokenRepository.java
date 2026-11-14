package com.example.backend.auth.infrastructure.persistance.repository;

import com.example.backend.auth.infrastructure.persistance.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {
    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            UPDATE RefreshTokenJpaEntity r
               SET r.revokedAt = :now
             WHERE r.userId = :userId
               AND r.revokedAt IS NULL
            """)
    int revokeAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}
