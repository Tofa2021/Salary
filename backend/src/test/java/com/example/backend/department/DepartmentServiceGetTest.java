package com.example.backend.department;

import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.department.exception.DepartmentNotFoundException;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        when(departmentRepository.findAll(any(org.springframework.data.domain.PageRequest.class))).thenReturn(new PageImpl<>(List.of(d1, d2)));

        PageResult<DepartmentResult> pageResult = departmentService.getAll(PageRequest.of(0, 2));

        assertThat(pageResult.items()).hasSize(2);
        assertThat(pageResult.items().getFirst().id()).isEqualTo(1L);
        assertThat(pageResult.items().get(0).parentId()).isNull();
        assertThat(pageResult.items().get(1).id()).isEqualTo(2L);
        assertThat(pageResult.items().get(1).parentId()).isEqualTo(1L);
    }

    @Test
    void getAll_Empty_Success() {
        when(departmentRepository.findAll(any(org.springframework.data.domain.PageRequest.class))).thenReturn(Page.empty());

        assertThat(departmentService.getAll(PageRequest.of(0, 20)).items()).isEmpty();
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
