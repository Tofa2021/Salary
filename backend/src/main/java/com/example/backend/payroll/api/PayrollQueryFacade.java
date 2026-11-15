package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.payroll.PayrollResult;
import com.example.backend.payroll.domain.shared.model.Period;

import java.util.List;

public interface PayrollQueryFacade {
    PayrollResult getById(Long id);

    List<PayrollResult> getByPeriod(Period period);
}
