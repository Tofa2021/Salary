package com.example.backend.auth.api.dto.auth;

import com.example.backend.auth.domain.model.UserRole;

public record AuthResult(String accessToken, String refreshToken, String username, UserRole role) {
}
