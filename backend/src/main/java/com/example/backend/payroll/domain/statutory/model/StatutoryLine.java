package com.example.backend.payroll.domain.statutory.model;

import com.example.backend.payroll.domain.shared.model.Money;

public record StatutoryLine(String code, String description, Money amount) {
}
