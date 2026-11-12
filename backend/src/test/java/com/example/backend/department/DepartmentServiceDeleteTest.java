package com.example.backend.department;

import com.example.backend.department.exception.DepartmentHasChildrenException;
import com.example.backend.department.exception.DepartmentHasEmployeesException;
import com.example.backend.department.exception.DepartmentNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DepartmentServiceDeleteTest extends BaseDepartmentServiceTest {

    @Test
    void delete_Success() {
        Department department = department(1L, "Department", "D-001", null);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.countByParentId(1L)).thenReturn(0L);
        when(employeeLookup.existsByDepartmentId(1L)).thenReturn(false);

        departmentService.delete(1L);

        verify(departmentRepository).delete(department);
    }

    @Test
    void delete_DepartmentNotFound_Throws() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(DepartmentNotFoundException.class)
                .hasMessageContaining("1");

        verify(departmentRepository, never()).delete(any());
    }

    @Test
    void delete_HasChildDepartments_Throws() {
        Department department = department(1L, "Department", "D-001", null);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeLookup.existsByDepartmentId(1L)).thenReturn(false);
        when(departmentRepository.countByParentId(1L)).thenReturn(1L);

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(DepartmentHasChildrenException.class);

        verify(departmentRepository, never()).delete(any());
    }

    @Test
    void delete_HasEmployees_Throws() {
        Department department = department(1L, "Department", "D-001", null);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeLookup.existsByDepartmentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(DepartmentHasEmployeesException.class);

        verify(departmentRepository, never()).delete(any());
    }
}
