package com.example.backend.service;

import com.example.backend.dto.user.CreateUserRequest;
import com.example.backend.dto.user.UserResult;
import com.example.backend.exception.user.LastAdminException;
import com.example.backend.exception.user.UserAlreadyExistsException;
import com.example.backend.exception.user.UserNotFoundException;
import com.example.backend.model.user.User;
import com.example.backend.model.user.UserRole;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void create_Success() {
        CreateUserRequest request = new CreateUserRequest("username", "password", UserRole.VIEWER);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");

        UserResult result = userService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo(request.username());
        assertThat(result.role()).isEqualTo(request.role().name());

        verify(userRepository).existsByUsername(request.username());
        verify(userRepository).save(any());
        verify(passwordEncoder).encode(any());
    }

    @Test
    void create_AlreadyExistsWithSuchName_Throws() {
        CreateUserRequest request = new CreateUserRequest("username", "password", UserRole.VIEWER);
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void create_DefaultRoleIsViewer_WhenRoleIsNull() {
        CreateUserRequest request = new CreateUserRequest("username", "password", null);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResult result = userService.create(request);

        assertThat(result.role()).isEqualTo(UserRole.VIEWER.name());
    }

    @Test
    void create_HashesPassword_AndDoesNotStoreRawPassword() {
        CreateUserRequest request = new CreateUserRequest("username", "password", UserRole.HR);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });

        userService.create(request);

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(argThat(u -> "encoded-password".equals(u.getPassword())));
    }

    @Test
    void create_DoesNotCallSave_WhenDuplicate() {
        CreateUserRequest request = new CreateUserRequest("username", "password", UserRole.HR);
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(UserAlreadyExistsException.class);

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void findAll_ReturnsAll() {
        List<User> users = List.of(
                new User(1L, "username1", "password", UserRole.HR),
                new User(2L, "username2", "password", UserRole.VIEWER),
                new User(3L, "admin", "password", UserRole.ADMIN)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserResult> result = userService.findAll();

        assertThat(result).hasSize(users.size());
        assertThat(result).extracting(UserResult::username).containsExactly("username1", "username2", "admin");
        assertThat(result).extracting(UserResult::role).containsExactly(UserRole.HR.name(), UserRole.VIEWER.name(), UserRole.ADMIN.name());
    }

    @Test
    void findAll_Empty_ReturnsEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.findAll()).isEmpty();
    }

    @Test
    void findById_Success() {
        User user = new User(1L, "username", "password", UserRole.HR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResult result = userService.findById(1L);

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.username()).isEqualTo(user.getUsername());
        assertThat(result.role()).isEqualTo(user.getRole().name());
    }

    @Test
    void findById_NotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateRole_NonAdminToHr_Success() {
        User user = new User(1L, "username", "encoded-password", UserRole.VIEWER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResult result = userService.updateRole(1L, UserRole.HR);

        assertThat(result.role()).isEqualTo(UserRole.HR.name());
        assertThat(user.getRole()).isEqualTo(UserRole.HR);
    }

    @Test
    void updateRole_AdminToAdmin_Success() {
        User user = new User(1L, "admin", "encoded-password", UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(3L);

        UserResult result = userService.updateRole(1L, UserRole.ADMIN);

        assertThat(result.role()).isEqualTo(UserRole.ADMIN.name());
    }

    @Test
    void updateRole_AdminToHr_WithMultipleAdmins_Success() {
        User user = new User(1L, "admin", "encoded-password", UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(3L);

        UserResult result = userService.updateRole(1L, UserRole.HR);

        assertThat(result.role()).isEqualTo(UserRole.HR.name());
    }

    @Test
    void updateRole_LastAdminToHr_Throws() {
        User user = new User(1L, "admin", "encoded-password", UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> userService.updateRole(1L, UserRole.HR))
                .isInstanceOf(LastAdminException.class);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void updateRole_LastAdminToViewer_Throws() {
        User user = new User(1L, "admin", "encoded-password", UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> userService.updateRole(1L, UserRole.VIEWER))
                .isInstanceOf(LastAdminException.class);
    }

    @Test
    void updateRole_ViewerToAdmin_Success() {
        User user = new User(1L, "A", "encoded-password", UserRole.VIEWER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);

        UserResult result = userService.updateRole(1L, UserRole.ADMIN);

        assertThat(result.role()).isEqualTo(UserRole.ADMIN.name());
    }

    @Test
    void updateRole_NotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateRole(99L, UserRole.HR))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void changePassword_Success_HashesAndSetsPassword() {
        User user = new User(1L, "A", "old-encoded-password", UserRole.VIEWER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-raw-password")).thenReturn("new-encoded-password");

        userService.changePassword(1L, "new-raw-password");

        verify(passwordEncoder).encode("new-raw-password");
        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
    }

    @Test
    void changePassword_DoesNotStorePlain() {
        User user = new User(1L, "A", "old-encoded-password", UserRole.HR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-raw-password")).thenReturn("new-encoded-password");

        userService.changePassword(1L, "new-raw-password");

        assertThat(user.getPassword()).isNotEqualTo("new-raw-password");
    }

    @Test
    void changePassword_NotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(99L, "new"))
                .isInstanceOf(UserNotFoundException.class);

        verify(passwordEncoder, never()).encode(any());
    }
}