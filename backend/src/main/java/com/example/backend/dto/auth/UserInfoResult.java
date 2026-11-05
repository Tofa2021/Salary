package com.example.backend.dto.auth;

import com.example.backend.model.user.UserRole;

public record UserInfoResult(String username, UserRole role) {
}
