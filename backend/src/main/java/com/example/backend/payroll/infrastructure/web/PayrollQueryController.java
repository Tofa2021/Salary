package com.example.backend.payroll.infrastructure.web;

import com.example.backend.payroll.api.PayrollQueryFacade;
import com.example.backend.payroll.api.dto.PayrollResult;
import com.example.backend.payroll.domain.shared.model.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
public class PayrollQueryController {
    private final PayrollQueryFacade payrollQueryFacade;

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
