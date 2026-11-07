package com.example.backend.exception.user;

import com.example.backend.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String username) {
        super("User", "username", username);
    }

    public UserNotFoundException(Long id) {
        super("User", "id", String.valueOf(id));
    }
}
