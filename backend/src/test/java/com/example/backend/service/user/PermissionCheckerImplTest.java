package com.example.backend.service.user;

import com.example.backend.auth.api.CurrentUser;
import com.example.backend.auth.application.facade.PermissionCheckerImpl;
import com.example.backend.auth.domain.exception.user.UserNotFoundException;
import com.example.backend.auth.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class PermissionCheckerImplTest extends BaseUserServiceTest {

    private PermissionCheckerImpl checker;

    @BeforeEach
    void setUp() {
        checker = new PermissionCheckerImpl(userRepository);
    }

    @Test
    void hasPermission_Admin_HasPayrollCalculate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "a", UserRole.ADMIN)));

        assertThat(checker.hasPermission(1L, "payroll.calculate")).isTrue();
    }

    @Test
    void hasPermission_Viewer_DoesNotHavePayrollCalculate() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "v", UserRole.VIEWER)));

        assertThat(checker.hasPermission(2L, "payroll.calculate")).isFalse();
    }

    @Test
    void hasPermission_NullOrEmpty_ReturnsFalse() {
        assertThat(checker.hasPermission(null, "payroll.calculate")).isFalse();
        assertThat(checker.hasPermission(1L, null)).isFalse();
        assertThat(checker.hasPermission(1L, "  ")).isFalse();
    }

    @Test
    void requireCurrentUser_ReturnsCurrentUser() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user(3L, "hr", UserRole.HR)));

        CurrentUser user = checker.requireCurrentUser(3L);

        assertThat(user.userId()).isEqualTo(3L);
        assertThat(user.username()).isEqualTo("hr");
        assertThat(user.roles()).containsExactly("HR");
        assertThat(user.permissions()).contains("employee.create", "timesheet.approve");
        assertThat(user.permissions()).doesNotContain("payroll.calculate");
    }

    @Test
    void requireCurrentUser_NotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checker.requireCurrentUser(99L))
                .isInstanceOf(UserNotFoundException.class);
    }
}