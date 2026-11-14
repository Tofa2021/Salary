package com.example.backend.timesheet.infrastructure.persistance.repository;

import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetEntry;
import com.example.backend.timesheet.domain.model.WorkHours;
import com.example.backend.timesheet.domain.port.TimeSheetRepository;
import com.example.backend.timesheet.infrastructure.persistance.entity.TimeSheetEntryJpaEntity;
import com.example.backend.timesheet.infrastructure.persistance.entity.TimeSheetJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaTimeSheetRepository implements TimeSheetRepository {
    private final SpringDataTimeSheetRepository springDataTimeSheetRepository;

    @Override
    public Optional<TimeSheet> findById(Long id) {
        return springDataTimeSheetRepository.findById(id).map(this::toDomain);
    }

    @Override
    public PageResult<TimeSheet> findByEmployeeId(Long employeeId, PageRequest request) {
        Page<TimeSheetJpaEntity> page = springDataTimeSheetRepository.findByEmployeeId(employeeId, toPageable(request));
        return toPageResult(page);
    }

    @Override
    public PageResult<TimeSheet> findByPeriod(LocalDate start, LocalDate end, PageRequest request) {
        Page<TimeSheetJpaEntity> page = springDataTimeSheetRepository
                .findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(start, end, toPageable(request));
        return toPageResult(page);
    }

    @Override
    public Optional<TimeSheet> findByEmployeeIdAndPeriod(Long employeeId, LocalDate start, LocalDate end) {
        return springDataTimeSheetRepository.findByEmployeeIdAndPeriod(employeeId, start, end).map(this::toDomain);
    }

    @Override
    public List<TimeSheet> findAllByPeriod(LocalDate start, LocalDate end) {
        return springDataTimeSheetRepository.findAllByPeriod(start, end).stream().map(this::toDomain).toList();
    }

    @Override
    public TimeSheet save(TimeSheet timesheet) {
        TimeSheetJpaEntity saved = springDataTimeSheetRepository.save(toEntity(timesheet));
        return toDomain(saved);
    }

    @Override
    public void delete(TimeSheet timesheet) {
        springDataTimeSheetRepository.deleteById(timesheet.getId());
    }

    private TimeSheet toDomain(TimeSheetJpaEntity entity) {
        List<TimeSheetEntry> entries = entity.getEntries().stream()
                .map(this::toDomainEntry).toList();
        return TimeSheet.rehydrate(
                entity.getId(),
                entity.getEmployeeId(),
                entity.getPeriodStart(),
                entity.getPeriodEnd(),
                entity.getStatus(),
                entries,
                entity.getApprovedAt(),
                entity.getApprovedBy()
        );
    }

    private TimeSheetJpaEntity toEntity(TimeSheet timeSheet) {
        TimeSheetJpaEntity entity = new TimeSheetJpaEntity();
        entity.setId(timeSheet.getId());
        entity.setEmployeeId(timeSheet.getEmployeeId());
        entity.setPeriodStart(timeSheet.getPeriodStart());
        entity.setPeriodEnd(timeSheet.getPeriodEnd());
        entity.setStatus(timeSheet.getStatus());
        entity.setEntries(timeSheet.getEntries().stream()
                .map(entry -> toEntityEntry(entry, entity))
                .collect(Collectors.toCollection(ArrayList::new)));
        entity.setApprovedAt(timeSheet.getApprovedAt());
        entity.setApprovedBy(timeSheet.getApprovedBy());
        return entity;
    }

    private TimeSheetEntryJpaEntity toEntityEntry(TimeSheetEntry entry, TimeSheetJpaEntity timeSheetJpaEntity) {
        TimeSheetEntryJpaEntity entity = new TimeSheetEntryJpaEntity();
        entity.setId(entry.getId());
        entity.setDate(entry.getDate());
        entity.setHours(entry.getHours().value());
        entity.setTimeSheet(timeSheetJpaEntity);
        entity.setType(entry.getType());
        return entity;
    }

    private TimeSheetEntry toDomainEntry(TimeSheetEntryJpaEntity entity) {
        return TimeSheetEntry.rehydrate(
                entity.getId(),
                entity.getDate(),
                new WorkHours(entity.getHours()),
                entity.getType()
        );
    }

    private Pageable toPageable(PageRequest request) {
        return org.springframework.data.domain.PageRequest.of(request.page(), request.size());
    }

    private PageResult<TimeSheet> toPageResult(Page<TimeSheetJpaEntity> page) {
        return new PageResult<>(
                page.getContent().stream().map(this::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}
