package com.example.backend.auth.infrastructure.persistance.repository;

import com.example.backend.auth.domain.model.RefreshToken;
import com.example.backend.auth.domain.port.RefreshTokenRepository;
import com.example.backend.auth.infrastructure.persistance.entity.RefreshTokenJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaRefreshTokenRepository implements RefreshTokenRepository {
    private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;
    private final Clock clock;

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return springDataRefreshTokenRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return toDomain(springDataRefreshTokenRepository.save(toEntity(token)));
    }

    @Override
    public void revokeAllByUserId(Long userId, LocalDateTime now) {
        springDataRefreshTokenRepository.revokeAllByUserId(userId, now);
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.rehydrate(
                entity.getId(),
                entity.getTokenHash(),
                entity.getUserId(),
                LocalDateTime.now(clock),
                entity.getExpiresAt(),
                entity.getRevokedAt()
        );
    }

    private RefreshTokenJpaEntity toEntity(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.setId(refreshToken.getId());
        entity.setTokenHash(refreshToken.getToken());
        entity.setUserId(refreshToken.getUserId());
        entity.setCreatedAt(refreshToken.getCreatedAt());
        entity.setExpiresAt(refreshToken.getExpiresAt());
        entity.setRevokedAt(refreshToken.getRevokedAt());
        return entity;
    }
}
