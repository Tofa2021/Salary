package com.example.backend.payroll.application.service;

import com.example.backend.payroll.api.dto.worknorm.UpsertWorkNormCommand;
import com.example.backend.payroll.api.dto.worknorm.WorkNormResult;
import com.example.backend.payroll.domain.base.exception.WorkNormNotFoundException;
import com.example.backend.payroll.domain.base.model.WorkNorm;
import com.example.backend.payroll.domain.base.port.WorkNormRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkNormService {
    private final WorkNormRepository workNormRepository;

    public WorkNormResult upsert(UpsertWorkNormCommand command) {
        WorkNorm norm = WorkNorm.of(command.year(), command.month(), command.normHours());
        return toResult(workNormRepository.save(norm));
    }

    public WorkNormResult getByYearAndMonth(int year, int month) {
        return workNormRepository.findByYearAndMonth(year, month)
                .map(this::toResult)
                .orElseThrow(() -> new WorkNormNotFoundException(year, month));
    }

    public void delete(int year, int month) {
        WorkNorm norm = workNormRepository.findByYearAndMonth(year, month)
                .orElseThrow(() -> new WorkNormNotFoundException(year, month));
        workNormRepository.delete(norm);
    }

    private WorkNormResult toResult(WorkNorm norm) {
        return new WorkNormResult(norm.year(), norm.month(), norm.normHours());
    }
}
