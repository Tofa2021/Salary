package com.example.backend.service;

import com.example.backend.dto.employee.CreateEmployeeRequest;
import com.example.backend.dto.employee.EmployeeResult;
import com.example.backend.dto.employee.UpdateEmployeeRequest;
import com.example.backend.exception.alreadyExists.EmployeePersonnelNumberAlreadyExistsException;
import com.example.backend.exception.notFound.DepartmentNotFoundException;
import com.example.backend.exception.notFound.EmployeeNotFoundException;
import com.example.backend.exception.notFound.PositionNotFoundException;
import com.example.backend.model.employee.*;
import com.example.backend.repository.DepartmentRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    private Employee baseEmployee(Long id, String personnelNumber) {
        return new Employee(id,
                personnelNumber,
                "Иванов",
                "Иван",
                "Иванович",
                "",
                "",
                "",
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2026, 1, 1),
                null,
                "ivan@gmail.com",
                "+375251231231",
                Gender.MALE,
                position(),
                department(),
                EmploymentType.FULL_TIME,
                0,
                false,
                null,
                null,
                null
        );
    }

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

        EmployeeResult result = employeeService.create(request);

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
    void getById_Success() {
        Employee employee = baseEmployee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResult result = employeeService.getById(1L);

        assertThat(employee.getId()).isEqualTo(result.id());
        assertThat(employee.getPersonnelNumber()).isEqualTo(result.personnelNumber());
        assertThat(employee.getHireDate()).isEqualTo(result.hireDate());
        assertThat(employee.getDismissalDate()).isEqualTo(result.dismissalDate());
        assertThat(employee.getName()).isEqualTo(result.name());
    }

    @Test
    void getById_NotFound_Throws() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(1L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void getByPersonnelNumber_Success() {
        Employee employee = baseEmployee(1L, "T-001");
        when(employeeRepository.findByPersonnelNumber("T-001")).thenReturn(Optional.of(employee));

        EmployeeResult result = employeeService.getByPersonnelNumber("T-001");

        assertThat(employee.getId()).isEqualTo(result.id());
        assertThat(employee.getPersonnelNumber()).isEqualTo(result.personnelNumber());
        assertThat(employee.getHireDate()).isEqualTo(result.hireDate());
        assertThat(employee.getDismissalDate()).isEqualTo(result.dismissalDate());
        assertThat(employee.getName()).isEqualTo(result.name());
    }

    @Test
    void getByPersonnelNumber_NotFound_Throws() {
        when(employeeRepository.findByPersonnelNumber("T-001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getByPersonnelNumber("T-001"))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("T-001");
    }

    @Test
    void getAll_Success() {
        Pageable pageable = PageRequest.of(0, 2);
        Employee employee1 = baseEmployee(1L, "T-001");
        Employee employee2 = baseEmployee(2L, "T-002");
        List<Employee> employees = List.of(employee1, employee2);

        Page<Employee> springPage = new PageImpl<>(employees, pageable, 57);

        when(employeeRepository.findAll(pageable)).thenReturn(springPage);

        Page<EmployeeResult> result = employeeService.getAll(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).personnelNumber()).isEqualTo("T-001");
        assertThat(result.getContent().get(1).surname()).isEqualTo("Иванов");

        assertThat(result.getTotalElements()).isEqualTo(57);
        assertThat(result.getTotalPages()).isEqualTo(29);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();

        verify(employeeRepository).findAll(pageable);
    }

    @Test
    void getAll_EmptyPage_ReturnsEmptyContent() {
        Pageable pageable = PageRequest.of(0, 20);
        when(employeeRepository.findAll(pageable))
                .thenReturn(Page.empty(pageable));

        Page<EmployeeResult> result = employeeService.getAll(pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void getByDepartment_Success() {
        Pageable pageable = PageRequest.of(0, 2);
        Department department = department();
        Employee employee1 = baseEmployee(1L, "T-001");
        employee1.setDepartment(department);
        Employee employee2 = baseEmployee(2L, "T-002");
        employee2.setDepartment(department);
        List<Employee> employees = List.of(employee1, employee2);

        Page<Employee> springPage = new PageImpl<>(employees, pageable, 57);
        when(employeeRepository.findByDepartmentId(1L, pageable)).thenReturn(springPage);

        Page<EmployeeResult> result = employeeService.getByDepartment(1L, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).personnelNumber()).isEqualTo("T-001");
        assertThat(result.getContent().get(1).surname()).isEqualTo("Иванов");
    }

    @Test
    void update_Success() {
        Department department = department();
        Position position = position();
        Employee employee = baseEmployee(1L, "T-001");
        employee.setDepartment(department);
        employee.setPosition(position);
        UpdateEmployeeRequest request = new UpdateEmployeeRequest(
                "A",
                "B",
                "C",
                "asd@gmain.com",
                "+3751231231",
                LocalDate.of(2026, 10, 1),
                null,
                1L,
                1L,
                "FULL_TIME"
        );
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        EmployeeResult result = employeeService.update(1L, request);

        assertThat(employee.getId()).isEqualTo(result.id());
        assertThat(employee.getSurname()).isEqualTo(result.surname());
        assertThat(employee.getName()).isEqualTo(result.name());
        assertThat(employee.getMiddleName()).isEqualTo(result.middleName());
        assertThat(employee.getEmail()).isEqualTo(result.email());
        assertThat(employee.getPhone()).isEqualTo(result.phone());
        assertThat(employee.getHireDate()).isEqualTo(result.hireDate());
        assertThat(employee.getDismissalDate()).isEqualTo(result.dismissalDate());
        assertThat(employee.getDepartment().getId()).isEqualTo(result.departmentId());
        assertThat(employee.getPosition().getId()).isEqualTo(result.positionId());
        assertThat(employee.getEmploymentType().name()).isEqualTo(result.employmentType());
    }
}