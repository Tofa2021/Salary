package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.worknorm.UpsertWorkNormCommand;
import com.example.backend.payroll.api.dto.worknorm.WorkNormResult;

public interface WorkNormFacade {
    WorkNormResult upsert(UpsertWorkNormCommand command);

    WorkNormResult getByYearAndMonth(int year, int month);

    void delete(int year, int month);

}
