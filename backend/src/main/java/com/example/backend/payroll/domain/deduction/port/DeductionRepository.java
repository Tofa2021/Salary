package com.example.backend.payroll.domain.deduction.port;

import com.example.backend.payroll.domain.deduction.model.Deduction;

import java.time.LocalDate;
import java.util.List;

public interface DeductionRepository {
    List<Deduction> findActiveByEmployeeOnDate(Long employeeId, LocalDate date);

    Deduction save(Deduction deduction);

    void delete(Deduction deduction);
}
