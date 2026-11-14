package com.example.backend.timesheet.infrastructure.web.dto;

import java.time.LocalDate;

public record GetByPeriodTimeSheetRequest(LocalDate start, LocalDate end) {
}
