package com.example.backend.service.employee;

import com.example.backend.department.Department;
import com.example.backend.department.DepartmentRepository;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.application.service.EmployeeService;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentType;
import com.example.backend.employee.domain.model.Gender;
import com.example.backend.employee.infrastructure.persistance.SpringDataEmployeeRepository;
import com.example.backend.employee.infrastructure.web.dto.CreateEmployeeRequest;
import com.example.backend.employee.infrastructure.web.dto.UpdateEmployeeRequest;
import com.example.backend.position.Position;
import com.example.backend.position.PositionRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class BaseEmployeeServiceTest {

    @Mock
    protected SpringDataEmployeeRepository employeeRepository;
    @Mock
    protected DepartmentRepository departmentRepository;
    @Mock
    protected PositionRepository positionRepository;
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
        employee.setDepartment(department());
        employee.setPosition(position());
        employee.setEmploymentType(EmploymentType.FULL_TIME);
        employee.setGender(Gender.MALE);
        return employee;
    }

    protected Department department() {
        Department department = new Department();
        department.setId(1L);
        department.setName("IT");
        department.setCode("it-1");
        return department;
    }

    protected Department department(Long id, String name) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        department.setCode(name.toLowerCase() + "-" + id);
        return department;
    }

    protected Position position() {
        Position position = new Position();
        position.setId(1L);
        position.setName("Developer");
        position.setTariffCategory(10);
        position.setTariffCoefficient(BigDecimal.valueOf(2.48));
        return position;
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
                "A", "B", "C", "asd@gmail.com", "+3751231231",
                LocalDate.of(2026, 10, 1), null,
                1L, 1L, "FULL_TIME");
    }

    protected void stubSuccessCreate(String personnelNumber) {
        when(employeeRepository.existsByPersonnelNumber(personnelNumber)).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(java.util.Optional.of(department()));
        when(positionRepository.findById(1L)).thenReturn(java.util.Optional.of(position()));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee employee = inv.getArgument(0);
            if (employee.getId() == null) employee.setId(1L);
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