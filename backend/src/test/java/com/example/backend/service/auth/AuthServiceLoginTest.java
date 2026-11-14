package com.example.backend.service.auth;

import com.example.backend.auth.api.dto.auth.AuthResult;
import com.example.backend.auth.api.dto.auth.LoginCommand;
import com.example.backend.auth.domain.exception.InvalidCredentialsException;
import com.example.backend.auth.domain.model.RefreshToken;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceLoginTest extends BaseAuthServiceTest {

    @Test
    void login_Valid_ReturnsTokens() {
        User u = user(1L, "user", UserRole.ACCOUNTANT);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(u));
        when(passwordHasher.matches("pass", "HASH")).thenReturn(true);
        when(jwtProvider.generateAccess(u)).thenReturn("ACCESS");
        when(refreshTokenGenerator.generateRaw()).thenReturn("RAW");
        when(refreshTokenGenerator.hash("RAW")).thenReturn("HASHED");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResult result = authService.login(new LoginCommand("user", "pass"));

        assertThat(result.accessToken()).isEqualTo("ACCESS");
        assertThat(result.refreshToken()).isEqualTo("RAW");
        assertThat(result.username()).isEqualTo("user");
        assertThat(result.role()).isEqualTo(UserRole.ACCOUNTANT);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_UserNotFound_ThrowsInvalidCredentials() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginCommand("user", "pass")))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(passwordHasher, jwtProvider);
    }

    @Test
    void login_WrongPassword_ThrowsInvalidCredentials() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user(1L, "user", UserRole.HR)));
        when(passwordHasher.matches("wrong", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginCommand("user", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtProvider, never()).generateAccess(any());
    }
}
