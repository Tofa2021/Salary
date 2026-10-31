package com.example.backend.dto.employee;

import java.time.LocalDate;

public record UpdateEmployeeRequest(
        String surname,
        String name,
        String middleName,
        String email,
        String phone,
        LocalDate hireDate,
        LocalDate dismissalDate,
        Long departmentId,
        Long positionId,
        String employmentType
) {
}
