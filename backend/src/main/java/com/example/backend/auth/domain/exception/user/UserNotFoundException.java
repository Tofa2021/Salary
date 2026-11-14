package com.example.backend.auth.domain.exception.user;

import com.example.backend.shared.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String username) {
        super("User", "username", username);
    }

    public UserNotFoundException(Long id) {
        super("User", "id", String.valueOf(id));
    }
}
