package com.example.backend.payroll.infrastructure.web;

import com.example.backend.payroll.api.PayrollCalculationFacade;
import com.example.backend.payroll.api.dto.CalculatePayrollCommand;
import com.example.backend.payroll.api.dto.PayrollResult;
import com.example.backend.payroll.api.dto.RecalculatePayrollCommand;
import com.example.backend.payroll.domain.shared.model.Period;
import com.example.backend.payroll.infrastructure.web.dto.CalculatePayrollRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
public class PayrollCalculationController {
    private final PayrollCalculationFacade payrollCalculationFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollResult calculate(@RequestBody CalculatePayrollRequest request) {
        return payrollCalculationFacade.calculate(
                new CalculatePayrollCommand(request.employeeId(), Period.of(request.year(), request.month()))
        );
    }

    @PostMapping("/{id}/recalculate")
    @ResponseStatus(HttpStatus.OK)
    public PayrollResult recalculate(@PathVariable Long id) {
        return payrollCalculationFacade.recalculate(new RecalculatePayrollCommand(id));
    }
}
