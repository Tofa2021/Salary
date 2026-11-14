package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.payroll.ApprovePayrollCommand;
import com.example.backend.payroll.api.dto.payroll.CancelPayrollCommand;
import com.example.backend.payroll.api.dto.payroll.PayrollResult;

public interface PayrollApprovalFacade {
    PayrollResult approve(ApprovePayrollCommand command);

    PayrollResult cancel(CancelPayrollCommand command);
}
