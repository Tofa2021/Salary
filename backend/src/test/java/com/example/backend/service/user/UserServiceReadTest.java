package com.example.backend.service.user;

import com.example.backend.auth.api.dto.user.UserResult;
import com.example.backend.auth.domain.exception.user.UserNotFoundException;
import com.example.backend.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class UserServiceReadTest extends BaseUserServiceTest {

    @Test
    void getById_Exists_Returns() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "user", UserRole.HR)));

        UserResult result = userService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("user");
        assertThat(result.role()).isEqualTo(UserRole.HR);
    }

    @Test
    void getById_NotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAll_ReturnsAll() {
        when(userRepository.findAll()).thenReturn(List.of(
                user(1L, "u1", UserRole.HR),
                user(2L, "u2", UserRole.VIEWER)));

        var result = userService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserResult::username).containsExactly("u1", "u2");
    }

    @Test
    void getAll_Empty_ReturnsEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.getAll()).isEmpty();
    }

    @Test
    void findByIdOptional_Exists_Returns() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "u", UserRole.HR)));

        assertThat(userService.findByIdOptional(1L)).isPresent();
    }

    @Test
    void findByIdOptional_NotFound_Empty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(userService.findByIdOptional(99L)).isEmpty();
    }

    @Test
    void findByUsernameOptional_Exists_Returns() {
        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user(1L, "u", UserRole.HR)));

        assertThat(userService.findByUsernameOptional("u")).isPresent();
    }

    @Test
    void findByUsernameOptional_NotFound_Empty() {
        when(userRepository.findByUsername("u")).thenReturn(Optional.empty());

        assertThat(userService.findByUsernameOptional("u")).isEmpty();
    }
}
