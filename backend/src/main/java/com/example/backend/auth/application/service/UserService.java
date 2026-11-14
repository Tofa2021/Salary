package com.example.backend.auth.application.service;

import com.example.backend.auth.api.dto.user.CreateUserCommand;
import com.example.backend.auth.api.dto.user.UserResult;
import com.example.backend.auth.domain.exception.user.LastAdminException;
import com.example.backend.auth.domain.exception.user.UserAlreadyExistsException;
import com.example.backend.auth.domain.exception.user.UserNotFoundException;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import com.example.backend.auth.domain.port.PasswordHasher;
import com.example.backend.auth.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordEncoder;

    public UserResult create(CreateUserCommand request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }

        User user = User.create(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role() == null ? UserRole.VIEWER : request.role()
        );

        return toResult(userRepository.save(user));
    }

    public UserResult getById(Long id) {
        User user = findUserById(id);
        return toResult(user);
    }

    public List<UserResult> getAll() {
        return userRepository.findAll().stream().map(this::toResult).toList();
    }

    public Optional<UserResult> findByIdOptional(Long userId) {
        return userRepository.findById(userId).map(this::toResult);
    }

    public Optional<UserResult> findByUsernameOptional(String username) {
        return userRepository.findByUsername(username).map(this::toResult);
    }

    public UserResult updateRole(Long id, UserRole role) {
        User user = findUserById(id);
        long adminCount = userRepository.countByRole(UserRole.ADMIN);

        if (user.getRole() == UserRole.ADMIN && role != UserRole.ADMIN && adminCount <= 1) {
            throw new LastAdminException("Cannot remove last admin");
        }

        user.changeRole(role);
        return toResult(userRepository.save(user));
    }

    public void changePassword(Long id, String newPassword) {
        User user = findUserById(id);
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserResult toResult(User user) {
        return new UserResult(user.getId(), user.getUsername(), user.getRole());
    }
}
