package com.example.backend.payroll.application.usecase;

import com.example.backend.payroll.api.dto.CancelPayrollCommand;
import com.example.backend.payroll.domain.core.exception.PayrollNotFoundException;
import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.port.PayrollRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CancelPayrollUseCase {
    private final PayrollRepository payrollRepository;

    public Payroll execute(CancelPayrollCommand command) {
        Payroll payroll = payrollRepository.findById(command.payrollId())
                .orElseThrow(() -> new PayrollNotFoundException(command.payrollId()));
        payroll.cancel();
        return payrollRepository.save(payroll);
    }
}
