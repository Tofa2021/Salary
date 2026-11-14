package com.example.backend.auth.application.facade;

import com.example.backend.auth.api.AuthFacade;
import com.example.backend.auth.api.dto.auth.AuthResult;
import com.example.backend.auth.api.dto.auth.LoginCommand;
import com.example.backend.auth.api.dto.auth.RefreshCommand;
import com.example.backend.auth.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {
    private final AuthService authService;

    @Override
    @Transactional
    public AuthResult login(LoginCommand command) {
        return authService.login(command);
    }

    @Override
    @Transactional
    public AuthResult refresh(RefreshCommand command) {
        return authService.refresh(command.refreshToken());
    }

    @Override
    @Transactional
    public void logout(RefreshCommand command) {
        authService.logout(command.refreshToken());
    }
}
