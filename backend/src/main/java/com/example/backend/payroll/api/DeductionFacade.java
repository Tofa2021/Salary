package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.deduction.ChangeDeductionPeriodCommand;
import com.example.backend.payroll.api.dto.deduction.ChangeDeductionValueCommand;
import com.example.backend.payroll.api.dto.deduction.CreateDeductionCommand;
import com.example.backend.payroll.api.dto.deduction.DeductionResultDto;

import java.time.LocalDate;
import java.util.List;

public interface DeductionFacade {
    DeductionResultDto create(CreateDeductionCommand command);

    DeductionResultDto getById(Long id);

    List<DeductionResultDto> getByEmployee(Long employeeId, LocalDate date);

    DeductionResultDto changeValue(Long id, ChangeDeductionValueCommand command);

    DeductionResultDto changePeriod(Long id, ChangeDeductionPeriodCommand command);

    void delete(Long id);
}
