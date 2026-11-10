package com.example.backend.auth.service;

import com.example.backend.auth.exception.user.LastAdminException;
import com.example.backend.auth.exception.user.UserAlreadyExistsException;
import com.example.backend.auth.exception.user.UserNotFoundException;
import com.example.backend.auth.model.User;
import com.example.backend.auth.model.UserRole;
import com.example.backend.auth.repository.UserRepository;
import com.example.backend.shared.dto.user.CreateUserRequest;
import com.example.backend.shared.dto.user.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResult create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role() == null ? UserRole.VIEWER : request.role()
        );

        return UserResult.fromUser(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResult> findAll() {
        return userRepository.findAll().stream()
                .map(UserResult::fromUser)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResult findById(Long id) {
        User user = getUserById(id);
        return UserResult.fromUser(user);
    }

    @Transactional
    public UserResult updateRole(Long id, UserRole role) {
        User user = getUserById(id);
        long adminCount = userRepository.countByRole(UserRole.ADMIN);

        if (user.getRole() == UserRole.ADMIN && role != UserRole.ADMIN && adminCount <= 1) {
            throw new LastAdminException("Cannot remove last admin");
        }

        user.setRole(role);
        return UserResult.fromUser(user);
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
