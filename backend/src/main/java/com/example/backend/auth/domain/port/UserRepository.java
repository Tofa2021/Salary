package com.example.backend.auth.domain.port;

import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    boolean existsByUsername(String username);

    User save(User user);

    long countByRole(UserRole userRole);

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(String username);

    List<User> findAll();
}
