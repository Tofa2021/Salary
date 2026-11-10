package com.example.backend.shared.dto.user;

import com.example.backend.auth.model.UserRole;

public record CreateUserRequest(String username, String password, UserRole role) {
}
