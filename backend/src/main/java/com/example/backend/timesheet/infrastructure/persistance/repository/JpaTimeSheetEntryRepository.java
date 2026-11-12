package com.example.backend.timesheet.infrastructure.persistance.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTimeSheetEntryRepository {
    private final SpringDataTimeSheetEntryRepository springDataTimeSheetEntryRepository;
}
