package com.example.backend.timesheet.service;

import com.example.backend.auth.model.User;
import com.example.backend.auth.repository.UserRepository;
import com.example.backend.department.Department;
import com.example.backend.department.DepartmentRepository;
import com.example.backend.department.exception.DepartmentNotFoundException;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.infrastructure.persistance.SpringDataEmployeeRepository;
import com.example.backend.shared.dto.timesheet.*;
import com.example.backend.timesheet.exception.*;
import com.example.backend.timesheet.model.TimeSheet;
import com.example.backend.timesheet.model.TimeSheetEntry;
import com.example.backend.timesheet.repository.TimeSheetEntryRepository;
import com.example.backend.timesheet.repository.TimeSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSheetService {
    private final TimeSheetRepository timeSheetRepository;
    private final TimeSheetEntryRepository timeSheetEntryRepository;
    private final SpringDataEmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public TimeSheetResult create(CreateTimeSheetRequest request) {
        Department department = findDepartment(request.departmentId());

        if (timeSheetRepository.findByDepartmentAndPeriod(request.departmentId(), request.year(), request.month()).isPresent()) {
            throw new TimeSheetAlreadyExistsException(request.departmentId(), request.year(), request.month());
        }

        TimeSheet timeSheet = new TimeSheet(request.year(), request.month(), department);

        LocalDate periodEnd = LocalDate.of(request.year(), request.month(), 1)
                .withDayOfMonth(LocalDate.of(request.year(), request.month(), 1).lengthOfMonth());

        List<Employee> employees = employeeRepository.findActiveByDepartmentOnDate(request.departmentId(), periodEnd);

        for (Employee employee : employees) {
            addEmployeeToTimeSheet(timeSheet, employee);
        }

        TimeSheet saved = timeSheetRepository.save(timeSheet);
        return TimeSheetResult.from(saved, canEdit(saved), canApprove(saved), canReopen(saved));
    }

    @Transactional(readOnly = true)
    public TimeSheetResult getById(Long id) {
        TimeSheet timeSheet = findTimeSheet(id);
        return TimeSheetResult.from(timeSheet, canEdit(timeSheet), canApprove(timeSheet), canReopen(timeSheet));
    }

    @Transactional(readOnly = true)
    public TimeSheetResult getByPeriod(int year, int month, Long departmentId) {
        TimeSheet timeSheet = timeSheetRepository.findByDepartmentAndPeriod(departmentId, year, month)
                .orElseThrow(() -> new TimeSheetNotFoundException(departmentId, year, month));
        return TimeSheetResult.from(timeSheet, canEdit(timeSheet), canApprove(timeSheet), canReopen(timeSheet));
    }

    @Transactional(readOnly = true)
    public List<TimeSheetSummaryResult> getAll(int year, int month) {
        return timeSheetRepository.findAllByYearAndMonth(year, month).stream()
                .map(TimeSheetSummaryResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TimeSheetEntryResult getEntry(Long timeSheetId, Long employeeId) {
        TimeSheetEntry entry = timeSheetEntryRepository.findByTimeSheetIdAndEmployeeId(timeSheetId, employeeId)
                .orElseThrow(() -> new TimeSheetEntryNotFoundException(timeSheetId, employeeId));
        return TimeSheetEntryResult.from(entry);
    }

    @Transactional
    public TimeSheetEntryResult updateEntry(Long entryId, TimeSheetEntryUpdateRequest request) {
        TimeSheetEntry entry = timeSheetEntryRepository.findById(entryId)
                .orElseThrow(() -> new TimeSheetEntryNotFoundException(entryId));

        if (entry.getTimeSheet().isApproved()) {
            throw new TimeSheetAlreadyApprovedException(entry.getTimeSheet().getId());
        }

        applyUpdate(entry, request);
        validateEntry(entry);

        return TimeSheetEntryResult.from(timeSheetEntryRepository.save(entry));
    }

    @Transactional
    public List<TimeSheetEntryResult> bulkUpdateEntries(Long timeSheetId, List<TimeSheetEntryUpdateRequest> requests) {
        TimeSheet timeSheet = findTimeSheet(timeSheetId);

        if (timeSheet.isApproved()) {
            throw new TimeSheetAlreadyApprovedException(timeSheetId);
        }
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<Long> ids = requests.stream()
                .map(TimeSheetEntryUpdateRequest::id)
                .filter(java.util.Objects::nonNull)
                .toList();
        Map<Long, TimeSheetEntry> byId = timeSheetEntryRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(TimeSheetEntry::getId, Function.identity()));

        List<TimeSheetEntryResult> result = new java.util.ArrayList<>();
        for (TimeSheetEntryUpdateRequest request : requests) {
            if (request.id() == null) {
                throw new IllegalArgumentException("id is required for bulk update");
            }
            TimeSheetEntry entry = byId.get(request.id());
            if (entry == null) {
                throw new TimeSheetEntryNotFoundException(request.id());
            }
            applyUpdate(entry, request);
            validateEntry(entry);
            result.add(TimeSheetEntryResult.from(entry));
        }
        return result;
    }

    @Transactional
    public TimeSheetResult approve(Long timeSheetId) {
        TimeSheet timeSheet = findTimeSheet(timeSheetId);

        if (timeSheet.isApproved()) {
            throw new TimeSheetAlreadyApprovedException(timeSheetId);
        }
        if (timeSheet.getEntries() == null || timeSheet.getEntries().isEmpty()) {
            throw new EmptyTimeSheetException(timeSheetId);
        }

        timeSheet.setApproved(true);
        timeSheet.setApprovedAt(LocalDateTime.now());
        timeSheet.setApprovedBy(currentUser());

        return TimeSheetResult.from(timeSheetRepository.save(timeSheet), canEdit(timeSheet), canApprove(timeSheet), canReopen(timeSheet));
    }

    @Transactional
    public TimeSheetResult reopen(Long timeSheetId) {
        TimeSheet timeSheet = findTimeSheet(timeSheetId);

        if (!timeSheet.isApproved()) {
            throw new TimeSheetNotApprovedException(timeSheetId);
        }
//        TODO add later while payroll epic
//        Long departmentId = timeSheet.getDepartment() != null ? timeSheet.getDepartment().getId() : null;
//        if (departmentId != null && payrollRecordRepository
//                .existsApprovedByPeriodAndDepartment(timeSheet.getYear(), timeSheet.getMonth(), departmentId)) {
//            throw new PayrollAlreadyApprovedException(timeSheet.getYear(), timeSheet.getMonth(), departmentId);
//        }

        timeSheet.setApproved(false);
        timeSheet.setApprovedAt(null);
        timeSheet.setApprovedBy(null);

        return TimeSheetResult.from(timeSheetRepository.save(timeSheet), canEdit(timeSheet), canApprove(timeSheet), canReopen(timeSheet));
    }

    @Transactional(readOnly = true)
    public boolean isApproved(int year, int month, Long departmentId) {
        TimeSheet timeSheet = timeSheetRepository.findByDepartmentAndPeriod(departmentId, year, month)
                .orElseThrow(() -> new TimeSheetNotFoundException(departmentId, year, month));
        return timeSheet.isApproved();
    }

    @Transactional
    public TimeSheetEntryResult addEmployeeToTimeSheet(Long timeSheetId, Long employeeId) {
        TimeSheet timeSheet = findTimeSheet(timeSheetId);

        if (timeSheet.isApproved()) {
            throw new TimeSheetAlreadyApprovedException(timeSheetId);
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (timeSheetEntryRepository.existsByTimeSheetIdAndEmployeeId(timeSheetId, employeeId)) {
            throw new DuplicateTimeSheetEntryException(timeSheetId, employeeId);
        }

        TimeSheetEntry entry = new TimeSheetEntry();
        entry.setTimeSheet(timeSheet);
        entry.setEmployee(employee);
        entry.setWorkedDays(0);
        entry.setWorkedHours(0);
        entry.setVacationDays(0);
        entry.setSickLeaveDays(0);
        entry.setBusinessTripDays(0);
        entry.setAbsentDays(0);
        entry.setOvertimeHours(0);
        entry.setNightHours(0);

        return TimeSheetEntryResult.from(timeSheetEntryRepository.save(entry));
    }

    public TimeSheetEntry addEmployeeToTimeSheet(TimeSheet timeSheet, Employee employee) {
        TimeSheetEntry entry = new TimeSheetEntry(
                timeSheet, employee,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );
        timeSheet.getEntries().add(entry);
        return entry;
    }

    @Transactional
    public void removeEmployeeFromTimeSheet(Long timeSheetId, Long employeeId) {
        TimeSheet timeSheet = findTimeSheet(timeSheetId);

        if (timeSheet.isApproved()) {
            throw new TimeSheetAlreadyApprovedException(timeSheetId);
        }

        TimeSheetEntry entry = timeSheetEntryRepository
                .findByTimeSheetIdAndEmployeeId(timeSheetId, employeeId)
                .orElseThrow(() -> new TimeSheetEntryNotFoundException(timeSheetId, employeeId));

        timeSheetEntryRepository.delete(entry);
    }

    private TimeSheet findTimeSheet(Long id) {
        return timeSheetRepository.findById(id)
                .orElseThrow(() -> new TimeSheetNotFoundException(id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    private void applyUpdate(TimeSheetEntry entry, TimeSheetEntryUpdateRequest request) {
        if (request.workedDays() != null) {
            entry.setWorkedDays(request.workedDays());
        }
        if (request.workedHours() != null) {
            entry.setWorkedHours(request.workedHours());
        }
        if (request.vacationDays() != null) {
            entry.setVacationDays(request.vacationDays());
        }
        if (request.sickLeaveDays() != null) {
            entry.setSickLeaveDays(request.sickLeaveDays());
        }
        if (request.businessTripDays() != null) {
            entry.setBusinessTripDays(request.businessTripDays());
        }
        if (request.absentDays() != null) {
            entry.setAbsentDays(request.absentDays());
        }
        if (request.overtimeHours() != null) {
            entry.setOvertimeHours(request.overtimeHours());
        }
        if (request.nightHours() != null) {
            entry.setNightHours(request.nightHours());
        }
    }

    private void validateEntry(TimeSheetEntry entry) {
        if (entry.getWorkedDays() < 0 || entry.getWorkedHours() < 0
                || entry.getVacationDays() < 0 || entry.getSickLeaveDays() < 0
                || entry.getBusinessTripDays() < 0 || entry.getAbsentDays() < 0
                || entry.getOvertimeHours() < 0 || entry.getNightHours() < 0) {
            throw new InvalidTimeSheetEntryException("Negative values not allowed");
        }

//        TODO add later while add product calendar sheet
//        TimeSheet ts = entry.getTimeSheet();
//        int normDays = productionCalendarService.workingDaysInMonth(ts.getYear(), ts.getMonth());

//        int totalDays = entry.getWorkedDays() + entry.getVacationDays()
//                + entry.getSickLeaveDays() + entry.getBusinessTripDays() + entry.getAbsentDays();
//        if (totalDays > normDays) {
//            throw new InvalidTimeSheetEntryException(
//                    "Total days " + totalDays + " exceed norm " + normDays);
//        }
        if (entry.getOvertimeHours() > entry.getWorkedHours()) {
            throw new InvalidTimeSheetEntryException("Overtime hours " + entry.getOvertimeHours() +
                    " exceed worked hours " + entry.getWorkedHours());
        }
        if (entry.getNightHours() > entry.getWorkedHours()) {
            throw new InvalidTimeSheetEntryException("Night hours " + entry.getNightHours() +
                    " exceed worked hours " + entry.getWorkedHours());
        }
    }

    private User currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return null;
        }
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    private boolean canEdit(TimeSheet ts) {
        return !ts.isApproved();
    }

    private boolean canApprove(TimeSheet ts) {
        return !ts.isApproved() && ts.getEntries() != null && !ts.getEntries().isEmpty();
    }

    private boolean canReopen(TimeSheet timeSheet) {
        if (!timeSheet.isApproved()) {
            return false;
        }
        Long departmentId = timeSheet.getDepartment() != null ? timeSheet.getDepartment().getId() : null;
        if (departmentId == null) {
            return true;
        }

        return true;
//        return !payrollRecordRepository.existsApprovedByPeriodAndDepartment(timeSheet.getYear(), timeSheet.getMonth(), departmentId);
    }
}
