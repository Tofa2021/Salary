package com.example.backend.service.user;

import com.example.backend.auth.api.dto.user.UserResult;
import com.example.backend.auth.domain.exception.user.LastAdminException;
import com.example.backend.auth.domain.exception.user.UserNotFoundException;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceUpdateRoleTest extends BaseUserServiceTest {

    @Test
    void updateRole_NonAdminToHr_Success() {
        User u = user(1L, "u", UserRole.VIEWER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResult result = userService.updateRole(1L, UserRole.HR);

        assertThat(result.role()).isEqualTo(UserRole.HR);
        assertThat(u.getRole()).isEqualTo(UserRole.HR);
    }

    @Test
    void updateRole_AdminToAdmin_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "a", UserRole.ADMIN)));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(3L);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(userService.updateRole(1L, UserRole.ADMIN).role()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void updateRole_AdminToHr_MultipleAdmins_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "a", UserRole.ADMIN)));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(3L);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(userService.updateRole(1L, UserRole.HR).role()).isEqualTo(UserRole.HR);
    }

    @Test
    void updateRole_LastAdminToHr_Throws() {
        User u = user(1L, "a", UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> userService.updateRole(1L, UserRole.HR))
                .isInstanceOf(LastAdminException.class);

        assertThat(u.getRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateRole_LastAdminToViewer_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "a", UserRole.ADMIN)));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> userService.updateRole(1L, UserRole.VIEWER))
                .isInstanceOf(LastAdminException.class);
    }

    @Test
    void updateRole_ViewerToAdmin_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "u", UserRole.VIEWER)));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(userService.updateRole(1L, UserRole.ADMIN).role()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void updateRole_NotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateRole(99L, UserRole.HR))
                .isInstanceOf(UserNotFoundException.class);
    }
}
