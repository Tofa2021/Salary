package com.example.backend.auth.api.dto.user;

import com.example.backend.auth.domain.model.UserRole;

public record UserResult(Long id, String username, UserRole role) {
}
