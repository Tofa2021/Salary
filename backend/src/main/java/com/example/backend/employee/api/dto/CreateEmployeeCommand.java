package com.example.backend.employee.api.dto;

import java.time.LocalDate;

public record CreateEmployeeCommand(
        String personnelNumber,
        String surname,
        String name,
        String middleName,
        String email,
        String phone,
        LocalDate hireDate,
        Long departmentId,
        Long positionId,
        String employmentType
) {
}
