package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.DeductionFacade;
import com.example.backend.payroll.api.dto.deduction.ChangeDeductionPeriodCommand;
import com.example.backend.payroll.api.dto.deduction.ChangeDeductionValueCommand;
import com.example.backend.payroll.api.dto.deduction.CreateDeductionCommand;
import com.example.backend.payroll.api.dto.deduction.DeductionResultDto;
import com.example.backend.payroll.application.service.DeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionFacadeImpl implements DeductionFacade {
    private final DeductionService deductionService;

    @Override
    @Transactional
    public DeductionResultDto create(CreateDeductionCommand command) {
        return deductionService.create(command);
    }

    @Override
    @Transactional(readOnly = true)
    public DeductionResultDto getById(Long id) {
        return deductionService.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeductionResultDto> getByEmployee(Long employeeId, LocalDate date) {
        return deductionService.getByEmployee(employeeId, date);
    }

    @Override
    @Transactional
    public DeductionResultDto changeValue(Long id, ChangeDeductionValueCommand command) {
        return deductionService.changeValue(id, command);
    }

    @Override
    @Transactional
    public DeductionResultDto changePeriod(Long id, ChangeDeductionPeriodCommand command) {
        return deductionService.changePeriod(id, command);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        deductionService.delete(id);
    }
}
