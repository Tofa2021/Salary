package com.example.backend.auth.infrastructure.persistance.repository;

import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import com.example.backend.auth.domain.port.UserRepository;
import com.example.backend.auth.infrastructure.persistance.entity.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return springDataUserRepository.findById(userId).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataUserRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return springDataUserRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataUserRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        return toDomain(springDataUserRepository.save(toEntity(user)));
    }

    @Override
    public long countByRole(UserRole userRole) {
        return springDataUserRepository.countByRole(userRole);
    }

    private User toDomain(UserJpaEntity entity) {
        return User.rehydrate(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getRole());
    }

    private UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setRole(user.getRole());
        return entity;
    }
}
