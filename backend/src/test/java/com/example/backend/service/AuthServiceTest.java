package com.example.backend.service;

import com.example.backend.dto.auth.AuthResponse;
import com.example.backend.dto.auth.LoginRequest;
import com.example.backend.model.user.User;
import com.example.backend.model.user.UserRole;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user(String username, UserRole role) {
        return new User(username, "hashed", role);
    }

    private Authentication authenticated(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(new CustomUserDetails(user));
        return auth;
    }

    @Test
    public void login_validCredentials_returnsTokenAndUserInfo() {
        User user = user("test", UserRole.ADMIN);
        Authentication auth = authenticated(user);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(user.getUsername(), user.getRole().name())).thenReturn("jwt-token-123");

        AuthResponse response = authService.login(new LoginRequest(user.getUsername(), user.getPassword()));

        assertThat(response.token()).isEqualTo("jwt-token-123");
        assertThat(response.username()).isEqualTo("test");
        assertThat(response.role().name()).isEqualTo("ADMIN");
    }

    @Test
    public void login_badPassword_BadCredentialsException() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("test", "wrong")))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generateToken(any(), any());
    }
}