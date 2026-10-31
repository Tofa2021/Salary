package com.example.backend.dto.employee;

import com.example.backend.model.employee.Employee;

import java.time.LocalDate;

public record EmployeeDto(
        Long id,
        String personnelNumber,
        String surname,
        String name,
        String middleName,
        String email,
        String phone,
        LocalDate hireDate,
        LocalDate dismissalDate,
        String employmentType,
        Long departmentId,
        String departmentName,
        Long positionId,
        String positionName
) {
    public static EmployeeDto fromEmployee(Employee employee) {
        return new EmployeeDto(
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
                employee.getDepartment() == null ? null : employee.getDepartment().getId(),
                employee.getDepartment() == null ? null : employee.getDepartment().getName(),
                employee.getPosition() == null ? null : employee.getPosition().getId(),
                employee.getPosition() == null ? null : employee.getPosition().getName()
        );
    }
}
