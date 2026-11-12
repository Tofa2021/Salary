package com.example.backend.department;

import com.example.backend.department.api.dto.CreateDepartmentCommand;
import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.department.exception.DepartmentCodeAlreadyExistsException;
import com.example.backend.department.exception.DepartmentNameAlreadyExistsException;
import com.example.backend.department.exception.DepartmentNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DepartmentServiceCreateTest extends BaseDepartmentServiceTest {

    @Test
    void create_WithoutParent_Success() {
        CreateDepartmentCommand request = createCommand("Department", "D-001", null);
        stubSaveAssignsId(1L);

        DepartmentResult result = departmentService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Department");
        assertThat(result.code()).isEqualTo("D-001");
        assertThat(result.parentId()).isNull();
        verify(departmentRepository).save(any());
    }

    @Test
    void create_WithParent_Success() {
        Department parent = department(1L, "Parent", "D-001", null);
        CreateDepartmentCommand request = createCommand("Department", "D-002", parent.getId());
        when(departmentRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
        stubSaveAssignsId(2L);

        DepartmentResult result = departmentService.create(request);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Department");
        assertThat(result.code()).isEqualTo("D-002");
        assertThat(result.parentId()).isEqualTo(parent.getId());
        verify(departmentRepository).save(any());
    }

    @Test
    void create_ParentNotFound_Throws() {
        CreateDepartmentCommand request = createCommand("Department", "D-002", 1L);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(DepartmentNotFoundException.class)
                .hasMessageContaining("1");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void create_NameAlreadyExists_Throws() {
        CreateDepartmentCommand request = createCommand("Department", "D-001", null);
        when(departmentRepository.existsByName("Department")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(DepartmentNameAlreadyExistsException.class)
                .hasMessageContaining("Department");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void create_CodeAlreadyExists_Throws() {
        CreateDepartmentCommand request = createCommand("Department", "D-001", null);
        when(departmentRepository.existsByCode("D-001")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(DepartmentCodeAlreadyExistsException.class)
                .hasMessageContaining("D-001");

        verify(departmentRepository, never()).save(any());
    }
}