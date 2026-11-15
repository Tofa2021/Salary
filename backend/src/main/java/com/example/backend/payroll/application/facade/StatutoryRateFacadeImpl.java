package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.StatutoryRateFacade;
import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRatePeriodCommand;
import com.example.backend.payroll.api.dto.statutory.CreateStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.StatutoryRateResult;
import com.example.backend.payroll.application.service.StatutoryRateService;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StatutoryRateFacadeImpl implements StatutoryRateFacade {
    private final StatutoryRateService statutoryRateService;

    @Override
    @Transactional
    public StatutoryRateResult create(CreateStatutoryRateCommand command) {
        return statutoryRateService.create(command);
    }

    @Override
    @Transactional(readOnly = true)
    public StatutoryRateResult getById(Long id) {
        return statutoryRateService.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public StatutoryRateResult getActual(String code, LocalDate date) {
        return statutoryRateService.getActual(code, date);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<StatutoryRateResult> getAll(String code, PageRequest pageRequest) {
        return statutoryRateService.getAll(code, pageRequest);
    }

    @Override
    @Transactional
    public StatutoryRateResult changeRate(Long id, ChangeStatutoryRateCommand command) {
        return statutoryRateService.changeRate(id, command);
    }

    @Override
    @Transactional
    public StatutoryRateResult changePeriod(Long id, ChangeStatutoryRatePeriodCommand command) {
        return statutoryRateService.changePeriod(id, command);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        statutoryRateService.delete(id);
    }
}
