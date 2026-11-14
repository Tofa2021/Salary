package com.example.backend.service.user;

import com.example.backend.auth.application.service.UserService;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import com.example.backend.auth.domain.port.PasswordHasher;
import com.example.backend.auth.domain.port.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class BaseUserServiceTest {
    @Mock
    protected UserRepository userRepository;

    @Mock
    protected PasswordHasher passwordHasher;

    @InjectMocks
    protected UserService userService;

    protected User user(Long id, String username, UserRole role) {
        return User.rehydrate(id, username, "encoded-password", role);
    }

    protected User newUser(String username, UserRole role) {
        return User.create(username, "encoded-password", role);
    }

    protected void stubSaveAssignsId(Long id) {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return User.rehydrate(id, u.getUsername(), u.getPassword(), u.getRole());
        });
    }
}
