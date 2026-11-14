package com.example.backend.timesheet.domain.model;

import com.example.backend.timesheet.domain.exception.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TimeSheet {
    private Long id;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long employeeId;
    private TimeSheetStatus status;
    private List<TimeSheetEntry> entries;
    private LocalDateTime approvedAt;
    private Long approvedBy;

    public static TimeSheet create(Long employeeId, LocalDate periodStart, LocalDate periodEnd) {
        if (employeeId == null) {
            throw new IllegalArgumentException("employeeId is required");
        }
        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException("period is required");
        }
        if (periodEnd.isBefore(periodStart)) {
            throw new InvalidTimeSheetPeriodException(periodStart, periodEnd);
        }

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.employeeId = employeeId;
        timeSheet.periodStart = periodStart;
        timeSheet.periodEnd = periodEnd;
        timeSheet.status = TimeSheetStatus.DRAFT;
        timeSheet.entries = new ArrayList<>();
        timeSheet.approvedAt = null;
        timeSheet.approvedBy = null;
        return timeSheet;
    }

    public static TimeSheet rehydrate(
            Long id,
            Long employeeId,
            LocalDate periodStart,
            LocalDate periodEnd,
            TimeSheetStatus status,
            List<TimeSheetEntry> entries,
            LocalDateTime approvedAt,
            Long approvedBy
    ) {
        TimeSheet timeSheet = new TimeSheet();
        timeSheet.id = id;
        timeSheet.employeeId = employeeId;
        timeSheet.periodStart = periodStart;
        timeSheet.periodEnd = periodEnd;
        timeSheet.status = status;
        timeSheet.entries = new ArrayList<>(entries);
        timeSheet.approvedAt = approvedAt;
        timeSheet.approvedBy = approvedBy;
        return timeSheet;
    }

    public void addEntry(TimeSheetEntry entry) {
        ensureEditable();
        if (entry.getDate().isBefore(periodStart) || entry.getDate().isAfter(periodEnd)) {
            throw new EntryOutsidePeriodException(entry.getDate(), periodStart, periodEnd);
        }

        BigDecimal dayTotal = entries.stream()
                .filter(e -> e.getDate().equals(entry.getDate()))
                .map(e -> e.getHours().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(entry.getHours().value());
        if (dayTotal.compareTo(BigDecimal.valueOf(24)) > 0) {
            throw new DayHoursExceededException(entry.getDate(), dayTotal);
        }

        entries.add(entry);
    }

    public void removeEntry(Long entryId) {
        ensureEditable();
        entries.removeIf(e -> e.getId() != null && e.getId().equals(entryId));
    }

    public void submit() {
        if (status != TimeSheetStatus.DRAFT && status != TimeSheetStatus.REJECTED) {
            throw new InvalidTimeSheetStatusException(id, status, TimeSheetStatus.SUBMITTED);
        }
        if (entries.isEmpty()) {
            throw new EmptyTimeSheetException(id);
        }
        this.status = TimeSheetStatus.SUBMITTED;
    }

    public void approve(Long approvedBy, LocalDateTime approvedAt) {
        if (status != TimeSheetStatus.SUBMITTED) {
            throw new InvalidTimeSheetStatusException(id, status, TimeSheetStatus.APPROVED);
        }
        if (approvedBy == null) {
            throw new IllegalArgumentException("approvedBy is required");
        }
        if (approvedAt == null) {
            throw new IllegalArgumentException("approvedAt is required");
        }
        this.status = TimeSheetStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
    }

    public void reject() {
        if (status != TimeSheetStatus.SUBMITTED) {
            throw new InvalidTimeSheetStatusException(id, status, TimeSheetStatus.REJECTED);
        }
        this.status = TimeSheetStatus.REJECTED;
        this.approvedAt = null;
        this.approvedBy = null;
    }

    public WorkHours totalHours() {
        BigDecimal total = entries.stream()
                .map(e -> e.getHours().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new WorkHours(total);
    }

    public WorkHours totalHoursByType(TimeSheetEntryType type) {
        return entries.stream()
                .filter(e -> e.getType() == type)
                .map(TimeSheetEntry::getHours)
                .reduce(WorkHours.of(0), WorkHours::add);
    }

    private void ensureEditable() {
        if (status == TimeSheetStatus.APPROVED) {
            throw new TimeSheetAlreadyApprovedException(id);
        }
        if (status == TimeSheetStatus.SUBMITTED) {
            throw new InvalidTimeSheetStatusException(id, status, TimeSheetStatus.DRAFT);
        }
    }
}
