package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.ApprovePayrollCommand;
import com.example.backend.payroll.api.dto.CancelPayrollCommand;
import com.example.backend.payroll.api.dto.PayrollResult;

public interface PayrollApprovalFacade {
    PayrollResult approve(ApprovePayrollCommand command);

    PayrollResult cancel(CancelPayrollCommand command);
}
