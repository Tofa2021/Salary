package com.example.backend.auth.application.facade;

import com.example.backend.auth.api.CurrentUser;
import com.example.backend.auth.api.PermissionChecker;
import com.example.backend.auth.domain.exception.user.UserNotFoundException;
import com.example.backend.auth.domain.model.RolePermissions;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import com.example.backend.auth.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class PermissionCheckerImpl implements PermissionChecker {
    private final UserRepository userRepository;

    @Override
    public CurrentUser requireCurrentUser(Long userId) {
        if (userId == null) {
            throw new UserNotFoundException((Long) null);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return new CurrentUser(
                user.getId(),
                user.getUsername(),
                Set.of(user.getRole().name()),
                RolePermissions.of(user.getRole())
        );
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        if (userId == null || permission == null || permission.isBlank()) {
            return false;
        }
        return userRepository.findById(userId)
                .map(user -> permissionsOf(user.getRole()).contains(permission))
                .orElse(false);
    }

    private Set<String> permissionsOf(UserRole role) {
        return RolePermissions.of(role);
    }
}
