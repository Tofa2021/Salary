package com.example.backend.shared.dto.auth;

import com.example.backend.auth.model.UserRole;

public record UserInfoResult(String username, UserRole role) {
}
