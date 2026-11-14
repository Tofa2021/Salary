package com.example.backend.auth.infrastructure.web;

import com.example.backend.auth.api.AuthFacade;
import com.example.backend.auth.api.CurrentUser;
import com.example.backend.auth.api.dto.auth.AuthResult;
import com.example.backend.auth.api.dto.auth.LoginCommand;
import com.example.backend.auth.api.dto.auth.RefreshCommand;
import com.example.backend.auth.api.dto.user.UserResult;
import com.example.backend.auth.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthFacade authFacade;

    @PostMapping("/login")
    public ResponseEntity<AuthResult> login(@RequestBody LoginCommand request) {
        AuthResult result = authFacade.login(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResult> refresh(@RequestBody RefreshCommand request) {
        AuthResult result = authFacade.refresh(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshCommand request) {
        authFacade.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResult> getMe(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CurrentUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserRole role = UserRole.valueOf(user.roles().iterator().next());
        return ResponseEntity.ok(new UserResult(user.userId(), user.username(), role));
    }
}
