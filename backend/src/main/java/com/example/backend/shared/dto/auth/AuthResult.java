package com.example.backend.shared.dto.auth;

import com.example.backend.auth.model.UserRole;

public record AuthResult(String accessToken, String refreshToken, String username, UserRole role) {
}
