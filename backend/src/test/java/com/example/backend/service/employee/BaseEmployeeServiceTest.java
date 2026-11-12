package com.example.backend.service.employee;

import com.example.backend.department.api.DepartmentLookup;
import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.application.service.EmployeeService;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentType;
import com.example.backend.employee.domain.model.Gender;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.employee.infrastructure.web.dto.CreateEmployeeRequest;
import com.example.backend.employee.infrastructure.web.dto.UpdateEmployeeRequest;
import com.example.backend.position.api.PositionLookup;
import com.example.backend.position.api.dto.PositionResult;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class BaseEmployeeServiceTest {

    @Mock
    protected EmployeeRepository employeeRepository;

    @Mock
    protected DepartmentLookup departmentLookup;

    @Mock
    protected PositionLookup positionLookup;

    @InjectMocks
    protected EmployeeService employeeService;

    protected Employee employee(Long id, String personnelNumber) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setPersonnelNumber(personnelNumber);
        employee.setSurname("Иванов");
        employee.setName("Иван");
        employee.setMiddleName("Иванович");
        employee.setEmail("ivan@gmail.com");
        employee.setPhone("+375251231231");
        employee.setHireDate(LocalDate.of(2026, 1, 1));
        employee.setDepartmentId(1L);
        employee.setPositionId(1L);
        employee.setEmploymentType(EmploymentType.FULL_TIME);
        employee.setGender(Gender.MALE);
        return employee;
    }

    protected DepartmentResult departmentResult() {
        return new DepartmentResult(1L, "IT", "it-1", null, new ArrayList<>());
    }

    protected DepartmentResult departmentResult(Long id, String name) {
        return new DepartmentResult(id, name, name.toLowerCase() + "-" + id, null, new ArrayList<>());
    }

    protected PositionResult positionResult() {
        return new PositionResult(1L, "Developer", 10, java.math.BigDecimal.valueOf(2.48), "");
    }

    protected CreateEmployeeRequest createRequest(String personnelNumber) {
        return new CreateEmployeeRequest(
                personnelNumber, "Иванов", "Иван", "Иванович",
                "ivan@gmail.com", "+375251231231",
                LocalDate.of(2026, 1, 1),
                1L, 1L, "FULL_TIME");
    }

    protected UpdateEmployeeRequest updateRequest() {
        return new UpdateEmployeeRequest(
                "A",
                "B",
                "C",
                "asd@gmail.com",
                "+3751231231",
                1L,
                1L,
                "FULL_TIME"
        );
    }

    protected void stubSuccessCreate(String personnelNumber) {
        when(employeeRepository.existsByPersonnelNumber(personnelNumber)).thenReturn(false);
        when(departmentLookup.findById(1L)).thenReturn(Optional.of(departmentResult()));
        when(positionLookup.findById(1L)).thenReturn(Optional.of(positionResult()));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee employee = inv.getArgument(0);
            if (employee.getId() == null) {
                employee.setId(1L);
            }
            return employee;
        });
    }

    protected void assertEmployeeResult(EmployeeResult result, Employee expected) {
        assertThat(result.id()).isEqualTo(expected.getId());
        assertThat(result.personnelNumber()).isEqualTo(expected.getPersonnelNumber());
        assertThat(result.surname()).isEqualTo(expected.getSurname());
        assertThat(result.name()).isEqualTo(expected.getName());
        assertThat(result.hireDate()).isEqualTo(expected.getHireDate());
    }
}
