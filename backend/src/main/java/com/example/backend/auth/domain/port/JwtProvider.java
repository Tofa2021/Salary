package com.example.backend.auth.domain.port;

import com.example.backend.auth.domain.model.User;

public interface JwtProvider {
    String generateAccess(User user);

    Long validateAndGetUserId(String token);
}
