package com.example.backend.service;

import com.example.backend.dto.user.CreateUserRequest;
import com.example.backend.dto.user.UserDto;
import com.example.backend.exception.LastAdminException;
import com.example.backend.exception.alreadyExists.UserAlreadyExistsException;
import com.example.backend.exception.notFound.UserNotFoundException;
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
    public UserDto create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role() == null ? UserRole.VIEWER : request.role()
        );
        userRepository.save(user);
        return UserDto.fromUser(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserDto::fromUser)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = getUserById(id);
        return UserDto.fromUser(user);
    }

    @Transactional
    public UserDto updateRole(Long id, UserRole role) {
        User user = getUserById(id);
        long adminCount = userRepository.countByRole(UserRole.ADMIN);

        if (user.getRole() == UserRole.ADMIN && role != UserRole.ADMIN && adminCount <= 1) {
            throw new LastAdminException("Cannot remove last admin");
        }

        user.setRole(role);
        return UserDto.fromUser(user);
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
