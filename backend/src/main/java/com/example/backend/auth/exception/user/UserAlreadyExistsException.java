package com.example.backend.auth.exception.user;

import com.example.backend.shared.exception.AlreadyExistsException;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException(String username) {
        super("User", "username", username);
    }

    public UserAlreadyExistsException(Long id) {
        super("User", "id", String.valueOf(id));
    }
}
