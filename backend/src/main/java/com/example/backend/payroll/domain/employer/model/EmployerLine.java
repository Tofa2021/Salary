package com.example.backend.payroll.domain.employer.model;

import com.example.backend.payroll.domain.shared.model.Money;

public record EmployerLine(String code, String description, Money amount) {
}
