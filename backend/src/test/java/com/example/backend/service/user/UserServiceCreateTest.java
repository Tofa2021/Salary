package com.example.backend.service.user;

import com.example.backend.auth.api.dto.user.CreateUserCommand;
import com.example.backend.auth.api.dto.user.UserResult;
import com.example.backend.auth.domain.exception.user.UserAlreadyExistsException;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceCreateTest extends BaseUserServiceTest {

    @Test
    void create_Valid_Success() {
        var cmd = new CreateUserCommand("user", "pass", UserRole.HR);
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(passwordHasher.encode("pass")).thenReturn("HASH");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return User.rehydrate(1L, u.getUsername(), u.getPassword(), u.getRole());
        });

        UserResult result = userService.create(cmd);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("user");
        assertThat(result.role()).isEqualTo(UserRole.HR);
        verify(passwordHasher).encode("pass");
        verify(userRepository).save(argThat(u -> "HASH".equals(u.getPassword())));
    }

    @Test
    void create_Duplicate_Throws() {
        when(userRepository.existsByUsername("user")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(new CreateUserCommand("user", "pass", UserRole.HR)))
                .isInstanceOf(UserAlreadyExistsException.class);

        verifyNoInteractions(passwordHasher);
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_NullRole_DefaultsToViewer() {
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(passwordHasher.encode("pass")).thenReturn("HASH");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return User.rehydrate(1L, u.getUsername(), u.getPassword(), u.getRole());
        });

        UserResult result = userService.create(new CreateUserCommand("user", "pass", null));

        assertThat(result.role()).isEqualTo(UserRole.VIEWER);
    }
}
