package com.example.backend.service;

import com.example.backend.dto.user.CreateUserRequest;
import com.example.backend.dto.user.UserResponse;
import com.example.backend.exception.LastAdminException;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.user.User;
import com.example.backend.model.user.UserRole;
import com.example.backend.repository.UserRepository;
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
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role() == null ? UserRole.VIEWER : request.role());
        userRepository.save(user);
        return UserResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = getUserById(id);
        return UserResponse.fromUser(user);
    }

    @Transactional
    public UserResponse updateRole(Long id, UserRole role) {
        User user = getUserById(id);
        long adminCount = userRepository.countByRole(UserRole.ADMIN);

        if (user.getRole() == UserRole.ADMIN && role != UserRole.ADMIN && adminCount <= 1) {
            throw new LastAdminException("Cannot remove last admin");
        }

        user.setRole(role);
        return UserResponse.fromUser(user);
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
