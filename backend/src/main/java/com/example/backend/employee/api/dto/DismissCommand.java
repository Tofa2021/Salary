package com.example.backend.employee.api.dto;

import java.time.LocalDate;

public record DismissCommand(LocalDate dismissalDate) {
}
