package com.example.backend.dto.auth;

public record RegisterRequest(String username, String password, String role) {
}
