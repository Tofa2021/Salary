package com.example.backend.auth.api;

import java.util.Set;

public record CurrentUser(
        Long userId,
        String username,
        Set<String> roles,
        Set<String> permissions
) {
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
