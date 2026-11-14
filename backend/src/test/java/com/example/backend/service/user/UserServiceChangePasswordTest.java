package com.example.backend.service.user;

import com.example.backend.auth.domain.exception.user.UserNotFoundException;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceChangePasswordTest extends BaseUserServiceTest {

    @Test
    void changePassword_HashesAndSaves() {
        User u = user(1L, "u", UserRole.HR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(passwordHasher.encode("new-raw")).thenReturn("new-hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.changePassword(1L, "new-raw");

        verify(passwordHasher).encode("new-raw");
        assertThat(u.getPassword()).isEqualTo("new-hash");
        verify(userRepository).save(u);
    }

    @Test
    void changePassword_NotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(99L, "new"))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(passwordHasher);
        verify(userRepository, never()).save(any());
    }
}
