package com.example.backend.dto.user;

import com.example.backend.model.user.User;

public record UserResult(Long id, String username, String role) {
    public static UserResult fromUser(User user) {
        return new UserResult(user.getId(), user.getUsername(), user.getRole().name());
    }
}
