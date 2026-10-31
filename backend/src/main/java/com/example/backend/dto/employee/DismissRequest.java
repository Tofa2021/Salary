package com.example.backend.dto.employee;

import java.time.LocalDate;

public record DismissRequest(LocalDate dismissalDate) {
}
