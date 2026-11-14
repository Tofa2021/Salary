package com.example.backend.auth.domain.model;

import lombok.Getter;

@Getter
public class User {
    private Long id;
    private String username;
    private String password;
    private UserRole role;

    public static User create(String username, String password, UserRole role) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.role = role;
        return user;
    }

    public static User rehydrate(Long id, String username, String password, UserRole role) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = password;
        user.role = role;
        return user;
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("newPassword is required");
        }
        password = newPassword;
    }

    public void changeRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("role is required");
        }
        this.role = role;
    }
}
