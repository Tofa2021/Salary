package com.example.backend.shared.dto.employee;

import java.time.LocalDate;

public record DismissRequest(LocalDate dismissalDate) {
}
