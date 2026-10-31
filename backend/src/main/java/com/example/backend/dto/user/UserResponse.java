package com.example.backend.dto.user;

import com.example.backend.model.user.User;

public record UserResponse(Long id, String username, String role) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }
}
