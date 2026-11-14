package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.CalculatePayrollCommand;
import com.example.backend.payroll.api.dto.PayrollResult;
import com.example.backend.payroll.api.dto.RecalculatePayrollCommand;

public interface PayrollCalculationFacade {
    PayrollResult calculate(CalculatePayrollCommand command);

    PayrollResult recalculate(RecalculatePayrollCommand command);
}
