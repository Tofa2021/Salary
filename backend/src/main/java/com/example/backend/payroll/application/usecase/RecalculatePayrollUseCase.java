package com.example.backend.payroll.application.usecase;

import com.example.backend.payroll.api.dto.payroll.CalculatePayrollCommand;
import com.example.backend.payroll.api.dto.payroll.RecalculatePayrollCommand;
import com.example.backend.payroll.domain.core.exception.PayrollAlreadyApprovedException;
import com.example.backend.payroll.domain.core.exception.PayrollNotFoundException;
import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.model.PayrollStatus;
import com.example.backend.payroll.domain.core.port.PayrollRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecalculatePayrollUseCase {
    private final PayrollRepository payrollRepository;
    private final CalculatePayrollUseCase calculatePayroll;

    public Payroll execute(RecalculatePayrollCommand command) {
        Payroll existing = payrollRepository.findById(command.payrollId())
                .orElseThrow(() -> new PayrollNotFoundException(command.payrollId()));

        if (existing.getStatus() == PayrollStatus.APPROVED)
            throw new PayrollAlreadyApprovedException(command.payrollId());

        Payroll recalculated = calculatePayroll.execute(new CalculatePayrollCommand(
                existing.getEmployeeId(), existing.getPeriod()));
        return payrollRepository.save(recalculated);
    }
}
