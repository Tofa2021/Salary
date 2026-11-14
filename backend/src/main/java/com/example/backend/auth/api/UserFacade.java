package com.example.backend.auth.api;

import com.example.backend.auth.api.dto.auth.ChangePasswordCommand;
import com.example.backend.auth.api.dto.user.CreateUserCommand;
import com.example.backend.auth.api.dto.user.UpdateRoleCommand;
import com.example.backend.auth.api.dto.user.UserResult;

import java.util.List;

public interface UserFacade {
    UserResult create(CreateUserCommand command);

    UserResult getById(Long id);

    UserResult updateRole(Long id, UpdateRoleCommand command);

    void changePassword(Long id, ChangePasswordCommand command);

    List<UserResult> getAll();
}
