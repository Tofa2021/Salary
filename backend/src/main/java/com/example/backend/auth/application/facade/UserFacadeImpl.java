package com.example.backend.auth.application.facade;

import com.example.backend.auth.api.UserFacade;
import com.example.backend.auth.api.dto.auth.ChangePasswordCommand;
import com.example.backend.auth.api.dto.user.CreateUserCommand;
import com.example.backend.auth.api.dto.user.UpdateRoleCommand;
import com.example.backend.auth.api.dto.user.UserResult;
import com.example.backend.auth.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;

    @Override
    @Transactional
    public UserResult create(CreateUserCommand command) {
        return userService.create(command);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResult getById(Long id) {
        return userService.getById(id);
    }

    @Override
    @Transactional
    public UserResult updateRole(Long id, UpdateRoleCommand command) {
        return userService.updateRole(id, command.role());
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordCommand command) {
        userService.changePassword(id, command.newPassword());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResult> getAll() {
        return userService.getAll();
    }
}
