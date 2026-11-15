package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.payroll.CalculatePayrollCommand;
import com.example.backend.payroll.api.dto.payroll.PayrollResult;
import com.example.backend.payroll.api.dto.payroll.RecalculatePayrollCommand;

public interface PayrollCalculationFacade {
    PayrollResult calculate(CalculatePayrollCommand command);

    PayrollResult recalculate(RecalculatePayrollCommand command);
}
