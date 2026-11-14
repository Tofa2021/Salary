package com.example.backend.auth.api;

import com.example.backend.auth.api.dto.auth.AuthResult;
import com.example.backend.auth.api.dto.auth.LoginCommand;
import com.example.backend.auth.api.dto.auth.RefreshCommand;

public interface AuthFacade {
    AuthResult login(LoginCommand command);

    AuthResult refresh(RefreshCommand command);

    void logout(RefreshCommand command);
}
