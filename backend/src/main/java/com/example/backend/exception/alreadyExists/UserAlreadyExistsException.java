package com.example.backend.exception.alreadyExists;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException(String username) {
        super("User", "username", username);
    }

    public UserAlreadyExistsException(Long id) {
        super("User", "id", String.valueOf(id));
    }
}
