package com.example.backend.employee.api.dto;

import java.time.LocalDate;

public record EmployeeResult(
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
        Long positionId
) {
}
