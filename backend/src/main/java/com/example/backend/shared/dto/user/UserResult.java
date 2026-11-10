package com.example.backend.shared.dto.user;

import com.example.backend.auth.model.User;

public record UserResult(Long id, String username, String role) {
    public static UserResult fromUser(User user) {
        return new UserResult(user.getId(), user.getUsername(), user.getRole().name());
    }
}
