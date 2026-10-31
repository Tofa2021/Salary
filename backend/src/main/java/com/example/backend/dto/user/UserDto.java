package com.example.backend.dto.user;

import com.example.backend.model.user.User;

public record UserDto(Long id, String username, String role) {
    public static UserDto fromUser(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getRole().name());
    }
}
