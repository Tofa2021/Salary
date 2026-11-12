package com.example.backend.timesheet.api.dto;

import com.example.backend.timesheet.domain.model.TimeSheetEntryType;

import java.time.LocalDate;

public record TimeSheetEntryCommand(LocalDate date, double hours, TimeSheetEntryType type) {
}
