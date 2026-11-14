package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.PayrollApprovalFacade;
import com.example.backend.payroll.api.dto.payroll.ApprovePayrollCommand;
import com.example.backend.payroll.api.dto.payroll.CancelPayrollCommand;
import com.example.backend.payroll.api.dto.payroll.PayrollResult;
import com.example.backend.payroll.application.usecase.ApprovePayrollUseCase;
import com.example.backend.payroll.application.usecase.CancelPayrollUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayrollApprovalFacadeImpl implements PayrollApprovalFacade {
    private final ApprovePayrollUseCase approvePayroll;
    private final CancelPayrollUseCase cancelPayroll;

    @Override
    @Transactional
    public PayrollResult approve(ApprovePayrollCommand command) {
        return PayrollResult.from(approvePayroll.execute(command));
    }

    @Override
    @Transactional
    public PayrollResult cancel(CancelPayrollCommand command) {
        return PayrollResult.from(cancelPayroll.execute(command));
    }
}
