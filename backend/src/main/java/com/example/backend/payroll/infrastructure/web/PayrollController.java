package com.example.backend.payroll.infrastructure.web;

import com.example.backend.auth.api.CurrentUser;
import com.example.backend.payroll.api.PayrollApprovalFacade;
import com.example.backend.payroll.api.PayrollCalculationFacade;
import com.example.backend.payroll.api.PayrollQueryFacade;
import com.example.backend.payroll.api.dto.payroll.*;
import com.example.backend.payroll.domain.shared.model.Period;
import com.example.backend.payroll.infrastructure.web.dto.CalculatePayrollRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollApprovalFacade payrollApprovalFacade;
    private final PayrollCalculationFacade payrollCalculationFacade;
    private final PayrollQueryFacade payrollQueryFacade;

    @PostMapping("/{id}/approve")
    public ResponseEntity<PayrollResult> approve(@PathVariable Long id, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CurrentUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(payrollApprovalFacade.approve(new ApprovePayrollCommand(id, user.userId())));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PayrollResult> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(payrollApprovalFacade.cancel(new CancelPayrollCommand(id)));
    }

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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PayrollResult getById(@PathVariable Long id) {
        return payrollQueryFacade.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PayrollResult> getByPeriod(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return payrollQueryFacade.getByPeriod(Period.of(year, month));
    }
}
