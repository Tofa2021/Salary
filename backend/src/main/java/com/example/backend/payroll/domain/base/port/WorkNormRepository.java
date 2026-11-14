package com.example.backend.payroll.domain.base.port;

import com.example.backend.payroll.domain.base.model.WorkNorm;

import java.util.Optional;

public interface WorkNormRepository {
    Optional<WorkNorm> findByYearAndMonth(int year, int month);

    WorkNorm save(WorkNorm norm);

    void delete(WorkNorm norm);
}
