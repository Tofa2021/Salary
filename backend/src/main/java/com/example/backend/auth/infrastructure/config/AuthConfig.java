package com.example.backend.auth.infrastructure.config;

import com.example.backend.auth.api.PermissionChecker;
import com.example.backend.auth.application.facade.PermissionCheckerImpl;
import com.example.backend.auth.application.service.AuthService;
import com.example.backend.auth.application.service.UserService;
import com.example.backend.auth.domain.port.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfig {
    @Bean
    public AuthService authService(
            UserRepository userRepository,
            RefreshTokenGenerator refreshTokenGenerator,
            RefreshTokenRepository refreshTokenRepository,
            PasswordHasher passwordEncoder,
            JwtProvider jwtProvider,
            Clock clock,
            AuthProperties authProperties
    ) {
        return new AuthService(
                userRepository,
                refreshTokenGenerator,
                refreshTokenRepository,
                passwordEncoder,
                jwtProvider,
                clock,
                authProperties.getRefresh().getTtl()
        );
    }

    @Bean
    public UserService userService(UserRepository userRepository, PasswordHasher passwordEncoder) {
        return new UserService(userRepository, passwordEncoder);
    }

    @Bean
    public PermissionChecker permissionChecker(UserRepository userRepository) {
        return new PermissionCheckerImpl(userRepository);
    }
}
