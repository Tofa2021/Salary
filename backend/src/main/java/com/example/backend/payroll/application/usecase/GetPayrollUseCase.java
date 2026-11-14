package com.example.backend.payroll.application.usecase;

import com.example.backend.payroll.domain.core.exception.PayrollNotFoundException;
import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.port.PayrollRepository;
import com.example.backend.payroll.domain.shared.model.Period;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetPayrollUseCase {
    private final PayrollRepository payrollRepository;

    public Payroll getById(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new PayrollNotFoundException(id));
    }

    public List<Payroll> getByPeriod(Period period) {
        return payrollRepository.findByPeriod(period);
    }
}
