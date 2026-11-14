package com.example.backend.auth.infrastructure.security;

import com.example.backend.auth.domain.port.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordHasher implements PasswordHasher {
    private final PasswordEncoder encoder;

    @Override
    public String encode(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is required");
        }
        return encoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || rawPassword.isBlank()
                || hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        return encoder.matches(rawPassword, hashedPassword);
    }
}
