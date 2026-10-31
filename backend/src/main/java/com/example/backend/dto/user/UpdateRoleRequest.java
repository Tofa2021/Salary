package com.example.backend.dto.user;

import com.example.backend.model.user.UserRole;

public record UpdateRoleRequest(UserRole role) {
}
