package com.example.backend.employee.application.service;

import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class EmployeeQueryService {
    private final EmployeeRepository employeeRepository;

    public Optional<EmployeeResult> findByIdOptional(Long id) {
        return employeeRepository.findById(id).map(this::toResult);
    }

    public boolean existsByDepartmentId(Long departmentId) {
        return employeeRepository.existsByDepartmentId(departmentId);
    }

    public PageResult<EmployeeResult> getAll(PageRequest pageRequest) {
        return employeeRepository.findAll(pageRequest).map(this::toResult);
    }

    public EmployeeResult getById(Long id) {
        return toResult(findEmployee(id));
    }

    public EmployeeResult getByPersonnelNumber(String personnelNumber) {
        Employee employee = employeeRepository.findByPersonnelNumber(personnelNumber)
                .orElseThrow(() -> new EmployeeNotFoundException(personnelNumber));
        return toResult(employee);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
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
