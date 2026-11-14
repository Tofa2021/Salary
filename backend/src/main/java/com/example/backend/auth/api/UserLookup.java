package com.example.backend.auth.api;

import com.example.backend.auth.api.dto.user.UserResult;

import java.util.Optional;

public interface UserLookup {
    Optional<UserResult> findById(Long userId);

    Optional<UserResult> findByUsername(String username);
}
