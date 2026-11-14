package com.example.backend.timesheet.domain.port;

import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import com.example.backend.timesheet.domain.model.TimeSheet;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeSheetRepository {
    TimeSheet save(TimeSheet timeSheet);

    Optional<TimeSheet> findById(Long id);

    void delete(TimeSheet timesheet);

    PageResult<TimeSheet> findByEmployeeId(Long employeeId, PageRequest pageRequest);

    PageResult<TimeSheet> findByPeriod(LocalDate start, LocalDate end, PageRequest pageRequest);

    Optional<TimeSheet> findByEmployeeIdAndPeriod(Long employeeId, LocalDate start, LocalDate end);

    List<TimeSheet> findAllByPeriod(LocalDate start, LocalDate end);
}
