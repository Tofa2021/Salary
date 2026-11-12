package com.example.backend.timesheet.application.lookup;

import com.example.backend.timesheet.api.TimeSheetLookup;
import com.example.backend.timesheet.api.dto.TimeSheetSummaryResult;
import com.example.backend.timesheet.application.service.TimeSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeSheetLookupImpl implements TimeSheetLookup {
    private final TimeSheetService timeSheetService;

    @Override
    @Transactional(readOnly = true)
    public Optional<TimeSheetSummaryResult> findByEmployeeAndPeriod(Long employeeId, LocalDate start, LocalDate end) {
        return timeSheetService.findHoursSummaryOptional(employeeId, start, end);
    }
}
