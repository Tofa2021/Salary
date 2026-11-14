package com.example.backend.auth.domain.port;

public interface PasswordHasher {
    String encode(String password);

    boolean matches(String rawPassword, String hashedPassword);
}
