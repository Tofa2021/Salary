package com.example.backend.dto.auth;

import com.example.backend.model.user.UserRole;

public record AuthResponse(String token, String username, UserRole role) {
}
