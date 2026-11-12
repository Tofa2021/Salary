package com.example.backend.employee.infrastructure.web.dto;

import java.time.LocalDate;

public record DismissRequest(LocalDate dismissalDate) {
}
