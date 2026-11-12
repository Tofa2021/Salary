package com.example.backend.timesheet.infrastructure.persistance.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTimeSheetRepository {
    private final SpringDataTimeSheetRepository springDataTimeSheetRepository;
}
