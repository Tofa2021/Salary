package com.example.backend.service;

import com.example.backend.department.Department;
import com.example.backend.department.DepartmentService;
import com.example.backend.department.SpringDataDepartmentRepository;
import com.example.backend.department.api.dto.CreateDepartmentCommand;
import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.department.api.dto.UpdateDepartmentCommand;
import com.example.backend.department.exception.*;
import com.example.backend.employee.infrastructure.persistance.SpringDataEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {
    @Mock
    private SpringDataDepartmentRepository departmentRepository;

    @Mock
    private SpringDataEmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void create_WithoutParent_Success() {
        CreateDepartmentCommand request = new CreateDepartmentCommand("Department", "D-001", null);
        when(departmentRepository.save(any())).thenAnswer(inv -> {
            Department department = inv.getArgument(0);
            department.setId(1L);
            return department;
        });

        DepartmentResult result = departmentService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.code()).isEqualTo(request.code());
        assertThat(result.parentId()).isEqualTo(request.parentId());
        verify(departmentRepository).save(any());
    }

    @Test
    void create_WithParent_Success() {
        Department parent = new Department(1L, "Parent", "D-001", null);
        CreateDepartmentCommand request = new CreateDepartmentCommand("Department", "D-002", parent.getId());
        when(departmentRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
        when(departmentRepository.save(any())).thenAnswer(inv -> {
            Department department = inv.getArgument(0);
            department.setId(2L);
            return department;
        });

        DepartmentResult result = departmentService.create(request);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.code()).isEqualTo(request.code());
        assertThat(result.parentId()).isEqualTo(request.parentId());
        verify(departmentRepository).save(any());
    }

    @Test
    void create_ParentNotFound_Throws() {
        CreateDepartmentCommand request = new CreateDepartmentCommand("Department", "D-002", 1L);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(DepartmentNotFoundException.class)
                .hasMessageContaining("1");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void create_NameAlreadyExists_Throws() {
        CreateDepartmentCommand request = new CreateDepartmentCommand("Department", "D-001", null);
        when(departmentRepository.existsByName(request.name())).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(DepartmentNameAlreadyExistsException.class)
                .hasMessageContaining("Department");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void create_CodeAlreadyExists_Throws() {
        CreateDepartmentCommand request = new CreateDepartmentCommand("Department", "D-001", null);
        when(departmentRepository.existsByCode(request.code())).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(DepartmentCodeAlreadyExistsException.class)
                .hasMessageContaining("D-001");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void getById_Success() {
        Department department = new Department(1L, "Department", "D-001", null);
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));

        DepartmentResult result = departmentService.getById(department.getId());

        assertThat(result.id()).isEqualTo(department.getId());
        assertThat(result.name()).isEqualTo(department.getName());
        assertThat(result.code()).isEqualTo(department.getCode());
        assertThat(result.parentId()).isNull();
    }

//    @Test
//    void getById_WithEmployees_Success() {
//        Employee employee1 = new Employee(1L, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, false, null, null, null);
//        Employee employee2 = new Employee(2L, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, false, null, null, null);
//        Department department = new Department(1L, "Department", "D-001", null, List.of(employee1, employee2));
//        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
//
//        DepartmentResult result = departmentService.getById(department.getId());
//
//        assertThat(result.id()).isEqualTo(department.getId());
//        assertThat(result.name()).isEqualTo(department.getName());
//        assertThat(result.code()).isEqualTo(department.getCode());
//        assertThat(result.parentId()).isNull();
//        assertThat(result.employeeIds()).isEqualTo(List.of(employee1.getId(), employee2.getId()));
//    }

    @Test
    void getById_DepartmentNotFound_Throws() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getById(1L))
                .isInstanceOf(DepartmentNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void getAll_Success() {
        Department department1 = new Department(1L, "Department1", "D-001", null);
        Department department2 = new Department(2L, "Department2", "D-002", null);
        List<Department> departments = List.of(department1, department2);
        when(departmentRepository.findAll()).thenReturn(departments);

        List<DepartmentResult> results = departmentService.getAll();

        assertThat(results.size()).isEqualTo(departments.size());

        assertThat(results.getFirst().id()).isEqualTo(department1.getId());
        assertThat(results.getFirst().name()).isEqualTo(department1.getName());
        assertThat(results.getFirst().code()).isEqualTo(department1.getCode());
        assertThat(results.getFirst().parentId()).isNull();

        assertThat(results.get(1).id()).isEqualTo(department2.getId());
        assertThat(results.get(1).name()).isEqualTo(department2.getName());
        assertThat(results.get(1).code()).isEqualTo(department2.getCode());
        assertThat(results.get(1).parentId()).isNull();
    }

    @Test
    void getAll_Empty_Success() {
        when(departmentRepository.findAll()).thenReturn(List.of());
        List<DepartmentResult> results = departmentService.getAll();
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    void update_FullUpdate_Success() {
        Department parent = new Department(1L, "Parent", "D-003", null);
        Department department = new Department(2L, "A", "D-001", null);
        UpdateDepartmentCommand request = new UpdateDepartmentCommand("B", "D-002", JsonNullable.of(1L));
        when(departmentRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(2L, request);

        assertThat(result.id()).isEqualTo(department.getId());
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.code()).isEqualTo(request.code());
        assertThat(result.parentId()).isEqualTo(request.parentId().get());

        verify(departmentRepository).save(any());
    }

    @Test
    void update_NullRequest_DoesNotUpdateAnything() {
        Department parent = new Department(2L, "Parent", "D-003", null);
        Department department = new Department(1L, "A", "D-001", parent);
        UpdateDepartmentCommand request = new UpdateDepartmentCommand(null, null, JsonNullable.undefined());
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(department.getId(), request);

        assertThat(result.id()).isEqualTo(department.getId());
        assertThat(result.name()).isEqualTo(department.getName());
        assertThat(result.code()).isEqualTo(department.getCode());
        assertThat(result.parentId()).isEqualTo(department.getParent().getId());

        verify(departmentRepository).save(any());
    }

    @Test
    void update_DepartmentNotFound_Throws() {
        UpdateDepartmentCommand request = new UpdateDepartmentCommand(null, null, JsonNullable.undefined());
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> departmentService.update(1L, request))
                .isInstanceOf(DepartmentNotFoundException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void update_ParentDepartmentNotFound_Throws() {
        Department department = new Department(1L, "A", "D-001", null);
        UpdateDepartmentCommand request = new UpdateDepartmentCommand(null, null, JsonNullable.of(2L));
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.update(1L, request))
                .isInstanceOf(DepartmentNotFoundException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void update_SetParentFromNull_Success() {
        Department department = new Department(1L, "A", "D-001", null);
        Department newParent = new Department(2L, "Parent", "D-002", null);
        UpdateDepartmentCommand request = new UpdateDepartmentCommand(
                null,
                null,
                JsonNullable.of(newParent.getId())
        );
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.findById(newParent.getId())).thenReturn(Optional.of(newParent));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(department.getId(), request);

        assertThat(result.id()).isEqualTo(department.getId());
        assertThat(result.name()).isEqualTo(department.getName());
        assertThat(result.code()).isEqualTo(department.getCode());
        assertThat(result.parentId()).isEqualTo(newParent.getId());

        verify(departmentRepository).save(department);
    }

    @Test
    void update_SetParentFromAnotherDepartment_Success() {
        Department oldParent = new Department(3L, "OldParent", "D-003", null);
        Department department = new Department(1L, "A", "D-001", oldParent);
        Department newParent = new Department(2L, "NewParent", "D-002", null);
        UpdateDepartmentCommand request = new UpdateDepartmentCommand(
                null,
                null,
                JsonNullable.of(newParent.getId())
        );
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.findById(newParent.getId())).thenReturn(Optional.of(newParent));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(department.getId(), request);

        assertThat(result.id()).isEqualTo(department.getId());
        assertThat(result.name()).isEqualTo(department.getName());
        assertThat(result.code()).isEqualTo(department.getCode());
        assertThat(result.parentId()).isEqualTo(newParent.getId());

        verify(departmentRepository).save(department);
    }

    @Test
    void update_SetNullParent_Success() {
        Department parent = new Department(2L, "Parent", "D-002", null);
        Department department = new Department(1L, "A", "D-001", parent);
        UpdateDepartmentCommand request = new UpdateDepartmentCommand(
                null,
                null,
                JsonNullable.ofNull()
        );
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);

        DepartmentResult result = departmentService.update(department.getId(), request);

        assertThat(result.id()).isEqualTo(department.getId());
        assertThat(result.name()).isEqualTo(department.getName());
        assertThat(result.code()).isEqualTo(department.getCode());
        assertThat(result.parentId()).isNull();

        verify(departmentRepository).save(department);
    }

    @Test
    void update_OwnParent_Throws() {
        Department department = new Department(1L, "A", "D-001", null);
        UpdateDepartmentCommand request = new UpdateDepartmentCommand(
                null,
                null,
                JsonNullable.of(department.getId())
        );
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> departmentService.update(department.getId(), request))
                .isInstanceOf(DepartmentSelfParentException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void update_NewParentIsDescendant_Throws() {
        Department department = new Department(1L, "A", "D-001", null);
        Department descendant = new Department(2L, "B", "D-002", department);
        UpdateDepartmentCommand request = new UpdateDepartmentCommand(
                null,
                null,
                JsonNullable.of(descendant.getId())
        );
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.findById(descendant.getId())).thenReturn(Optional.of(descendant));

        assertThatThrownBy(() -> departmentService.update(department.getId(), request))
                .isInstanceOf(DepartmentCycleException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void delete_Success() {
        Department department = new Department(1L, "Department", "D-001", null);
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.countByParentId(department.getId())).thenReturn(0L);
        when(employeeRepository.countByDepartmentId(department.getId())).thenReturn(0L);

        departmentService.delete(department.getId());

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
        Department department = new Department(1L, "Department", "D-001", null);
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(employeeRepository.countByDepartmentId(department.getId())).thenReturn(0L);
        when(departmentRepository.countByParentId(department.getId())).thenReturn(1L);

        assertThatThrownBy(() -> departmentService.delete(department.getId()))
                .isInstanceOf(DepartmentHasChildrenException.class);

        verify(departmentRepository, never()).delete(any());
    }

//    @Test
//    void delete_HasEmployees_Throws() {
//        Employee employee1 = new Employee(1L, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, false, null, null, null);
//        Employee employee2 = new Employee(2L, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, false, null, null, null);
//        Department department = new Department(1L, "Department", "D-001", null, List.of(employee1, employee2));
//        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
//        when(employeeRepository.countByDepartmentId(department.getId())).thenReturn(1L);
//
//        assertThatThrownBy(() -> departmentService.delete(department.getId()))
//                .isInstanceOf(DepartmentHasEmployeesException.class);
//
//        verify(departmentRepository, never()).delete(any());
//    }
}