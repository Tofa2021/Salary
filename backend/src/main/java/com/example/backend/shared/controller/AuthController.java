package com.example.backend.shared.controller;

import com.example.backend.auth.model.User;
import com.example.backend.auth.service.AuthService;
import com.example.backend.shared.dto.auth.AuthResult;
import com.example.backend.shared.dto.auth.LoginRequest;
import com.example.backend.shared.dto.auth.RefreshRequest;
import com.example.backend.shared.dto.auth.UserInfoResult;
import com.example.backend.shared.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResult> login(@RequestBody LoginRequest request) {
        AuthResult result = authService.login(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResult> refresh(@RequestBody RefreshRequest request) {
        AuthResult result = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResult> getMe(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails(User user))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new UserInfoResult(user.getUsername(), user.getRole()));
    }
}
