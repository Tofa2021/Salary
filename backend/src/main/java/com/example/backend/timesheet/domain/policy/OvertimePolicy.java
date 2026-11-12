package com.example.backend.timesheet.domain.policy;

import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import com.example.backend.timesheet.domain.model.WorkHours;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OvertimePolicy {
    private static final BigDecimal DAILY_LIMIT = BigDecimal.valueOf(8);

    public List<HoursPartition> partition(LocalDate date, WorkHours total) {
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }
        if (total == null) {
            throw new IllegalArgumentException("total is required");
        }

        if (total.value().compareTo(DAILY_LIMIT) <= 0) {
            return List.of(new HoursPartition(date, total, TimeSheetEntryType.REGULAR));
        }

        WorkHours regular = new WorkHours(DAILY_LIMIT);
        WorkHours overtime = new WorkHours(total.value().subtract(DAILY_LIMIT));
        return List.of(
                new HoursPartition(date, regular, TimeSheetEntryType.REGULAR),
                new HoursPartition(date, overtime, TimeSheetEntryType.OVERTIME)
        );
    }
}
