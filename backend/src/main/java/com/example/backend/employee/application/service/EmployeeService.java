package com.example.backend.employee.application.service;

import com.example.backend.department.api.DepartmentLookup;
import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.domain.exception.employee.EmployeeInvalidDepartmentException;
import com.example.backend.employee.domain.exception.employee.EmployeeInvalidPositionException;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.exception.employee.EmployeePersonnelNumberAlreadyExistsException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentType;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.employee.infrastructure.web.dto.CreateEmployeeRequest;
import com.example.backend.employee.infrastructure.web.dto.DismissRequest;
import com.example.backend.employee.infrastructure.web.dto.UpdateEmployeeRequest;
import com.example.backend.position.api.PositionLookup;
import com.example.backend.position.api.dto.PositionResult;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentLookup departmentLookup;
    private final PositionLookup positionLookup;

    public EmployeeResult create(CreateEmployeeRequest request) {
        if (employeeRepository.existsByPersonnelNumber(request.personnelNumber())) {
            throw new EmployeePersonnelNumberAlreadyExistsException(request.personnelNumber());
        }

        DepartmentResult departmentResult = findDepartmentResult(request.departmentId());
        PositionResult positionResult = positionLookup.findById(request.positionId())
                .orElseThrow(() -> new EmployeeInvalidPositionException(request.positionId()));

        Employee employee = new Employee();
        employee.setPersonnelNumber(request.personnelNumber());
        employee.setSurname(request.surname());
        employee.setName(request.name());
        employee.setMiddleName(request.middleName());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setHireDate(request.hireDate());
        employee.setDepartmentId(departmentResult.id());
        employee.setPositionId(positionResult.id());
        employee.setEmploymentType(EmploymentType.from(request.employmentType()));

        return toResult(employeeRepository.save(employee));
    }

    public EmployeeResult getById(Long id) {
        return toResult(findEmployee(id));
    }

    public EmployeeResult getByPersonnelNumber(String personnelNumber) {
        Employee employee = employeeRepository.findByPersonnelNumber(personnelNumber)
                .orElseThrow(() -> new EmployeeNotFoundException(personnelNumber));
        return toResult(employee);
    }

    public PageResult<EmployeeResult> getAll(PageRequest pageRequest) {
        return employeeRepository.findAll(pageRequest).map(this::toResult);
    }

    public PageResult<EmployeeResult> getByDepartment(Long departmentId, PageRequest pageRequest) {
        return employeeRepository.findByDepartmentId(departmentId, pageRequest)
                .map(this::toResult);
    }

    public EmployeeResult update(Long id, UpdateEmployeeRequest request) {
        Employee employee = findEmployee(id);

        if (request.surname() != null) {
            employee.setSurname(request.surname());
        }
        if (request.name() != null) {
            employee.setName(request.name());
        }
        if (request.middleName() != null) {
            employee.setMiddleName(request.middleName());
        }
        if (request.email() != null) {
            employee.setEmail(request.email());
        }
        if (request.phone() != null) {
            employee.setPhone(request.phone());
        }
        if (request.departmentId() != null) {
            DepartmentResult departmentResult = findDepartmentResult(request.departmentId());
            employee.setDepartmentId(departmentResult.id());
        }
        if (request.positionId() != null) {
            PositionResult positionResult = positionLookup.findById(request.positionId())
                    .orElseThrow(() -> new EmployeeInvalidPositionException(request.positionId()));
            employee.setPositionId(positionResult.id());
        }
        if (request.employmentType() != null) {
            employee.setEmploymentType(EmploymentType.from(request.employmentType()));
        }

        employeeRepository.save(employee);

        return toResult(employee);
    }

    public EmployeeResult dismiss(Long id, DismissRequest request) {
        Employee employee = findEmployee(id);
        employee.dismiss(request.dismissalDate());
        employeeRepository.save(employee);
        return toResult(employee);
    }

    public EmployeeResult rehire(Long id, LocalDate newHireDate) {
        Employee employee = findEmployee(id);
        employee.rehire(newHireDate);
        employeeRepository.save(employee);
        return toResult(employee);
    }

    public void delete(Long id) {
        Employee employee = findEmployee(id);
        employeeRepository.delete(employee);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private DepartmentResult findDepartmentResult(Long id) {
        return departmentLookup.findById(id)
                .orElseThrow(() -> new EmployeeInvalidDepartmentException(id));
    }

    public void transfer(Long newDepartmentId, Long employeeId) {
        Employee employee = findEmployee(employeeId);
        employee.transferTo(newDepartmentId);
        employeeRepository.save(employee);
    }

    public void removeFromDepartment(Long employeeId) {
        Employee employee = findEmployee(employeeId);
        employee.removeFromDepartment();
        employeeRepository.save(employee);
    }

    public void addToDepartment(Long departmentId, Long employeeId) {
        Employee employee = findEmployee(employeeId);
        employee.ensureCanBeAddedToDepartment();
        DepartmentResult newDepartmentResult = findDepartmentResult(departmentId);
        employee.addToDepartment(newDepartmentResult.id());
        employeeRepository.save(employee);
    }

    private EmployeeResult toResult(Employee employee) {
        return new EmployeeResult(
                employee.getId(),
                employee.getPersonnelNumber(),
                employee.getSurname(),
                employee.getName(),
                employee.getMiddleName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getHireDate(),
                employee.getDismissalDate(),
                employee.getEmploymentType() == null ? null : employee.getEmploymentType().name(),
                employee.getDepartmentId(),
                employee.getPositionId()
        );
    }
}
