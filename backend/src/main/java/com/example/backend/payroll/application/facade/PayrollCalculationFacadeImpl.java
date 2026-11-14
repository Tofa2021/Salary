package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.PayrollCalculationFacade;
import com.example.backend.payroll.api.dto.CalculatePayrollCommand;
import com.example.backend.payroll.api.dto.PayrollResult;
import com.example.backend.payroll.api.dto.RecalculatePayrollCommand;
import com.example.backend.payroll.application.usecase.CalculatePayrollUseCase;
import com.example.backend.payroll.application.usecase.RecalculatePayrollUseCase;
import com.example.backend.payroll.domain.core.model.Payroll;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayrollCalculationFacadeImpl implements PayrollCalculationFacade {
    private final CalculatePayrollUseCase calculatePayroll;
    private final RecalculatePayrollUseCase recalculatePayroll;

    @Override
    @Transactional
    public PayrollResult calculate(CalculatePayrollCommand command) {
        Payroll payroll = calculatePayroll.execute(command);
        return PayrollResult.from(payroll);
    }

    @Override
    @Transactional
    public PayrollResult recalculate(RecalculatePayrollCommand command) {
        Payroll payroll = recalculatePayroll.execute(command);
        return PayrollResult.from(payroll);
    }
}
