package com.example.backend.service;

import com.example.backend.dto.auth.AuthResult;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.model.auth.RefreshToken;
import com.example.backend.model.user.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.security.JwtService;
import com.example.backend.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthResult login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        User user = details.user();

        String access = jwtService.generateToken(user.getUsername(), user.getRole().name());
        String refresh = refreshTokenService.create(user);
        return new AuthResult(access, refresh, user.getUsername(), user.getRole());
    }

    public AuthResult refresh(String refreshToken) {
        RefreshToken rt = refreshTokenService.verify(refreshToken);
        User user = rt.getUser();

        refreshTokenService.revoke(refreshToken);
        String newRefresh = refreshTokenService.create(user);

        String newAccess = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResult(newAccess, newRefresh, user.getUsername(), user.getRole());
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revoke(refreshToken);
        }
    }
}
