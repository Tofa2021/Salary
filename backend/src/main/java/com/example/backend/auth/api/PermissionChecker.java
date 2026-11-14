package com.example.backend.auth.api;

public interface PermissionChecker {
    CurrentUser requireCurrentUser(Long userId);

    boolean hasPermission(Long userId, String permission);
}
