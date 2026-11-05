package com.example.backend.dto.auth;

import com.example.backend.model.user.UserRole;

public record AuthResult(String accessToken, String refreshToken, String username, UserRole role) {
}
