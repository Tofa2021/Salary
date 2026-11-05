package com.example.backend.repository;

import com.example.backend.model.auth.RefreshToken;
import com.example.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUser(User user);

    List<RefreshToken> findAllByUserAndRevokedFalse(User user);

    @Modifying
    @Query("""
            UPDATE RefreshToken r
               SET r.revoked = true,
                   r.revokedAt = :now
             WHERE r.user = :user
               AND r.revoked = false
            """)
    int revokeAllByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    @Modifying
    @Query("""
            UPDATE RefreshToken r
               SET r.revoked = true,
                   r.revokedAt = :now
             WHERE r.user = :user
               AND r.id <> :exceptId
               AND r.revoked = false
            """)
    int revokeAllByUserExcept(@Param("user") User user, @Param("exceptId") Long exceptId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken r
             WHERE r.revoked = true
               AND r.revokedAt < :cutoff
            """)
    int deleteOldRevoked(@Param("cutoff") LocalDateTime cutoff);

    long countByUserAndRevokedFalse(User user);
}
