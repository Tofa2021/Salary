package com.example.backend.service;

import com.example.backend.dto.employee.CreateEmployeeRequest;
import com.example.backend.dto.employee.EmployeeDto;
import com.example.backend.exception.alreadyExists.EmployeePersonnelNumberAlreadyExistsException;
import com.example.backend.exception.notFound.DepartmentNotFoundException;
import com.example.backend.exception.notFound.PositionNotFoundException;
import com.example.backend.model.employee.Department;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.employee.Position;
import com.example.backend.repository.DepartmentRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private CreateEmployeeRequest validCreateRequest() {
        return new CreateEmployeeRequest(
                "T-001",
                "Иванов",
                "Иван",
                "Иванович",
                "ivan@gmail.com",
                "+375251231231",
                LocalDate.of(2026, 1, 1),
                1L,
                1L,
                "FULL_TIME"
        );
    }

    private Department department() {
        return new Department(
                1L,
                "IT",
                "it-1",
                null,
                List.of()
        );
    }

    private Position position() {
        return new Position(
                1L,
                "",
                1,
                BigDecimal.valueOf(1),
                ""
        );
    }

    @Test
    void create_validRequest_ReturnsEmployeeDto() {
        Long id = 1L;
        String personnelNumber = "T-001";
        String surname = "Иванов";
        String name = "Иван";
        String middleName = "Иванович";
        String email = "ivanov@example.com";
        String phone = "+375291234567";
        LocalDate hireDate = LocalDate.of(2026, 1, 1);
        String employmentType = "FULL_TIME";
        Long departmentId = 1L;
        String departmentName = "IT";
        Long positionId = 1L;
        String positionName = "Developer";
        CreateEmployeeRequest request = new CreateEmployeeRequest(
                personnelNumber,
                surname,
                name,
                middleName,
                email,
                phone,
                hireDate,
                departmentId,
                positionId,
                employmentType
        );
        Department department = new Department(departmentId, departmentName, "code", null, List.of());
        Position position = new Position(positionId, positionName, 1, BigDecimal.valueOf(1L), "");

        when(employeeRepository.existsByPersonnelNumber(any())).thenReturn(false);
        when(departmentRepository.findById(any())).thenReturn(Optional.of(department));
        when(positionRepository.findById(any())).thenReturn(Optional.of(position));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee saved = invocation.getArgument(0);
            saved.setId(id);
            return saved;
        });

        EmployeeDto result = employeeService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.personnelNumber()).isEqualTo(personnelNumber);
        assertThat(result.surname()).isEqualTo(surname);
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.middleName()).isEqualTo(middleName);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.phone()).isEqualTo(phone);
        assertThat(result.hireDate()).isEqualTo(hireDate);
        assertThat(result.dismissalDate()).isNull();
        assertThat(result.employmentType()).isEqualTo(employmentType);
        assertThat(result.departmentId()).isEqualTo(departmentId);
        assertThat(result.departmentName()).isEqualTo(departmentName);
        assertThat(result.positionId()).isEqualTo(positionId);
        assertThat(result.positionName()).isEqualTo(positionName);

        verify(employeeRepository).existsByPersonnelNumber(personnelNumber);
        verify(departmentRepository).findById(departmentId);
        verify(positionRepository).findById(positionId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void create_duplicatePersonnelNumber_throwsAndDoesNotSave() {
        when(employeeRepository.existsByPersonnelNumber("T-001")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(validCreateRequest()))
                .isInstanceOf(EmployeePersonnelNumberAlreadyExistsException.class)
                .hasMessageContaining("T-001");

        verify(employeeRepository, never()).save(any());
        verify(departmentRepository, never()).findById(any());
        verify(positionRepository, never()).findById(any());
    }

    @Test
    void create_departmentNotFound_throwsAndDoesNotSave() {
        when(employeeRepository.existsByPersonnelNumber(any())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.create(validCreateRequest()))
                .isInstanceOf(DepartmentNotFoundException.class)
                .hasMessageContaining("1");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void create_positionNotFound_throwsAndDoesNotSave() {
        when(employeeRepository.existsByPersonnelNumber(any())).thenReturn(false);
        when(departmentRepository.findById(any())).thenReturn(Optional.of(department()));
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.create(validCreateRequest()))
                .isInstanceOf(PositionNotFoundException.class)
                .hasMessageContaining("1");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void create_invalidEmploymentType_throwsIllegalArgument() {
        when(employeeRepository.existsByPersonnelNumber(any())).thenReturn(false);
        when(departmentRepository.findById(any())).thenReturn(Optional.of(department()));
        when(positionRepository.findById(any())).thenReturn(Optional.of(position()));

        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "T-001", "X", "Y", null, null, null,
                LocalDate.now(), 1L, 1L, "INVALID_TYPE");

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_TYPE");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void findById() {
    }

    @Test
    void findByPersonnelNumber() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findByDepartment() {
    }

    @Test
    void update() {
    }

    @Test
    void dismiss() {
    }

    @Test
    void rehire() {
    }

    @Test
    void delete() {
    }
}