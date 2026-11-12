package com.example.backend.department;

import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.department.api.dto.UpdateDepartmentCommand;
import com.example.backend.department.exception.DepartmentCycleException;
import com.example.backend.department.exception.DepartmentNotFoundException;
import com.example.backend.department.exception.DepartmentSelfParentException;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DepartmentServiceUpdateTest extends BaseDepartmentServiceTest {

    @Test
    void update_FullUpdate_Success() {
        Department parent = department(1L, "Parent", "D-003", null);
        Department department = department(2L, "A", "D-001", null);
        UpdateDepartmentCommand request = updateCommand("B", "D-002", JsonNullable.of(1L));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(2L, request);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("B");
        assertThat(result.code()).isEqualTo("D-002");
        assertThat(result.parentId()).isEqualTo(1L);
        verify(departmentRepository).save(any());
    }

    @Test
    void update_NullRequest_DoesNotUpdateAnything() {
        Department parent = department(2L, "Parent", "D-003", null);
        Department department = department(1L, "A", "D-001", parent);
        UpdateDepartmentCommand request = updateCommand(null, null, JsonNullable.undefined());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(1L, request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("A");
        assertThat(result.code()).isEqualTo("D-001");
        assertThat(result.parentId()).isEqualTo(2L);
        verify(departmentRepository).save(any());
    }

    @Test
    void update_DepartmentNotFound_Throws() {
        UpdateDepartmentCommand request = updateCommand(null, null, JsonNullable.undefined());
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.update(1L, request))
                .isInstanceOf(DepartmentNotFoundException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void update_ParentDepartmentNotFound_Throws() {
        Department department = department(1L, "A", "D-001", null);
        UpdateDepartmentCommand request = updateCommand(null, null, JsonNullable.of(2L));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.update(1L, request))
                .isInstanceOf(DepartmentNotFoundException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void update_SetParentFromNull_Success() {
        Department department = department(1L, "A", "D-001", null);
        Department newParent = department(2L, "Parent", "D-002", null);
        UpdateDepartmentCommand request = updateCommand(null, null, JsonNullable.of(2L));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(newParent));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(1L, request);

        assertThat(result.parentId()).isEqualTo(2L);
        verify(departmentRepository).save(department);
    }

    @Test
    void update_SetParentFromAnotherDepartment_Success() {
        Department oldParent = department(3L, "OldParent", "D-003", null);
        Department department = department(1L, "A", "D-001", oldParent);
        Department newParent = department(2L, "NewParent", "D-002", null);
        UpdateDepartmentCommand request = updateCommand(null, null, JsonNullable.of(2L));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(newParent));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(1L, request);

        assertThat(result.parentId()).isEqualTo(2L);
        verify(departmentRepository).save(department);
    }

    @Test
    void update_SetNullParent_Success() {
        Department parent = department(2L, "Parent", "D-002", null);
        Department department = department(1L, "A", "D-001", parent);
        UpdateDepartmentCommand request = updateCommand(null, null, JsonNullable.ofNull());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(1L, request);

        assertThat(result.parentId()).isNull();
        verify(departmentRepository).save(department);
    }

    @Test
    void update_OwnParent_Throws() {
        Department department = department(1L, "A", "D-001", null);
        UpdateDepartmentCommand request = updateCommand(null, null, JsonNullable.of(1L));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> departmentService.update(1L, request))
                .isInstanceOf(DepartmentSelfParentException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void update_NewParentIsDescendant_Throws() {
        Department department = department(1L, "A", "D-001", null);
        Department descendant = department(2L, "B", "D-002", department);
        UpdateDepartmentCommand request = updateCommand(null, null, JsonNullable.of(2L));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(descendant));

        assertThatThrownBy(() -> departmentService.update(1L, request))
                .isInstanceOf(DepartmentCycleException.class);

        verify(departmentRepository, never()).save(any());
    }
}
