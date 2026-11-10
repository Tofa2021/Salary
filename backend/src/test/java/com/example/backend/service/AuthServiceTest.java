package com.example.backend.service;

import com.example.backend.auth.model.User;
import com.example.backend.auth.model.UserRole;
import com.example.backend.auth.service.AuthService;
import com.example.backend.shared.dto.auth.AuthResult;
import com.example.backend.shared.dto.auth.LoginRequest;
import com.example.backend.shared.security.CustomUserDetails;
import com.example.backend.shared.security.JwtService;
import com.example.backend.shared.security.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private Authentication authenticated(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(new CustomUserDetails(user));
        return auth;
    }

    @Test
    public void login_validCredentials_returnsTokenAndUserInfo() {
        User user = new User("test", "password", UserRole.ADMIN);
        Authentication auth = authenticated(user);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(user.getUsername(), user.getRole().name())).thenReturn("jwt-token-123");
        when(refreshTokenService.create(any())).thenReturn("refresh-token");

        AuthResult response = authService.login(new LoginRequest(user.getUsername(), user.getPassword()));

        assertThat(response.accessToken()).isEqualTo("jwt-token-123");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
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

    @Test
    public void login_notFoundUser_UserNotFoundException() {
        User user = new User("test", "password", UserRole.ADMIN);
        when(authenticationManager.authenticate(any())).thenThrow(UsernameNotFoundException.class);

        assertThatThrownBy(() -> authService.login(new LoginRequest("test", "wrong")))
                .isInstanceOf(UsernameNotFoundException.class);

        verify(jwtService, never()).generateToken(any(), any());
    }
}