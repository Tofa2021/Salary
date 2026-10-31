package com.example.backend.dto.auth;

public record AuthResponse(String token, String username, String role) {
}
