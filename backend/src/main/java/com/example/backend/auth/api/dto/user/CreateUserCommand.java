package com.example.backend.auth.api.dto.user;

import com.example.backend.auth.domain.model.UserRole;

public record CreateUserCommand(String username, String password, UserRole role) {
}
