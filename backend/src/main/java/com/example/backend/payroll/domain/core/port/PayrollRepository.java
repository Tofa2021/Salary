package com.example.backend.payroll.domain.core.port;

import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.shared.model.Period;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository {
    Optional<Payroll> findById(Long id);

    Payroll save(Payroll payroll);

    void delete(Payroll payroll);

    Optional<Payroll> findByEmployeeIdAndPeriod(Long employeeId, Period period);

    List<Payroll> findByPeriod(Period period);
}
