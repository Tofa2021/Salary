package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.WorkNormFacade;
import com.example.backend.payroll.api.dto.worknorm.UpsertWorkNormCommand;
import com.example.backend.payroll.api.dto.worknorm.WorkNormResult;
import com.example.backend.payroll.application.service.WorkNormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkNormFacadeImpl implements WorkNormFacade {
    private final WorkNormService normService;

    @Override
    public WorkNormResult upsert(UpsertWorkNormCommand command) {
        return normService.upsert(command);
    }

    @Override
    public WorkNormResult getByYearAndMonth(int year, int month) {
        return normService.getByYearAndMonth(year, month);
    }

    @Override
    public void delete(int year, int month) {
        normService.delete(year, month);
    }
}
