package com.example.backend.department;

import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.department.exception.DepartmentNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class DepartmentServiceGetTest extends BaseDepartmentServiceTest {

    @Test
    void getById_Success() {
        Department department = department(1L, "Department", "D-001", null);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        DepartmentResult result = departmentService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Department");
        assertThat(result.code()).isEqualTo("D-001");
        assertThat(result.parentId()).isNull();
    }

    @Test
    void getById_DepartmentNotFound_Throws() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getById(1L))
                .isInstanceOf(DepartmentNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void getAll_Success() {
        Department d1 = department(1L, "Department1", "D-001", null);
        Department d2 = department(2L, "Department2", "D-002", d1);
        when(departmentRepository.findAll()).thenReturn(List.of(d1, d2));

        List<DepartmentResult> results = departmentService.getAll();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).id()).isEqualTo(1L);
        assertThat(results.get(0).parentId()).isNull();
        assertThat(results.get(1).id()).isEqualTo(2L);
        assertThat(results.get(1).parentId()).isEqualTo(1L);
    }

    @Test
    void getAll_Empty_Success() {
        when(departmentRepository.findAll()).thenReturn(List.of());

        assertThat(departmentService.getAll()).isEmpty();
    }

    @Test
    void findByIdOptional_Success() {
        Department d = department(1L, "Department", "D-001", null);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(d));

        Optional<DepartmentResult> result = departmentService.findByIdOptional(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
    }

    @Test
    void findByIdOptional_NotFound_ReturnsEmpty() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThat(departmentService.findByIdOptional(1L)).isEmpty();
    }
}
