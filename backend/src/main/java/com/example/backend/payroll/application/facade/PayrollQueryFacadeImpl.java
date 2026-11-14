package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.PayrollQueryFacade;
import com.example.backend.payroll.api.dto.PayrollResult;
import com.example.backend.payroll.application.usecase.GetPayrollUseCase;
import com.example.backend.payroll.domain.shared.model.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollQueryFacadeImpl implements PayrollQueryFacade {
    private final GetPayrollUseCase getPayroll;

    @Override
    @Transactional(readOnly = true)
    public PayrollResult getById(Long id) {
        return PayrollResult.from(getPayroll.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResult> getByPeriod(Period period) {
        return getPayroll.getByPeriod(period).stream().map(PayrollResult::from).toList();
    }
}
