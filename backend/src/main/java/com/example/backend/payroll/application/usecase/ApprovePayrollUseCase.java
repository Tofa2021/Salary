package com.example.backend.payroll.application.usecase;

import com.example.backend.payroll.api.dto.ApprovePayrollCommand;
import com.example.backend.payroll.domain.core.exception.PayrollNotFoundException;
import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.port.PayrollRepository;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ApprovePayrollUseCase {
    private final PayrollRepository payrollRepository;
    private final Clock clock;

    public Payroll execute(ApprovePayrollCommand command) {
        Payroll payroll = payrollRepository.findById(command.payrollId())
                .orElseThrow(() -> new PayrollNotFoundException(command.payrollId()));

        payroll.approve(command.approvedBy(), LocalDateTime.now(clock));
        return payrollRepository.save(payroll);
    }
}
