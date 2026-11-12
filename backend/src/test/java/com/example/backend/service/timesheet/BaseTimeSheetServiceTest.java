package com.example.backend.service.timesheet;

import com.example.backend.auth.model.User;
import com.example.backend.auth.model.UserRole;
import com.example.backend.department.Department;
import com.example.backend.department.SpringDataDepartmentRepository;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.infrastructure.persistance.SpringDataEmployeeRepository;
import com.example.backend.timesheet.api.dto.CreateTimeSheetCommand;
import com.example.backend.timesheet.api.dto.TimeSheetEntryUpdateCommand;
import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.application.service.TimeSheetService;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetEntry;
import com.example.backend.timesheet.infrastructure.persistance.repository.SpringDataTimeSheetEntryRepository;
import com.example.backend.timesheet.infrastructure.persistance.repository.SpringDataTimeSheetRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
abstract class BaseTimeSheetServiceTest {
    @Mock
    protected SpringDataTimeSheetRepository timeSheetRepository;

    @Mock
    protected SpringDataTimeSheetEntryRepository timeSheetEntryRepository;

    @Mock
    protected SpringDataEmployeeRepository employeeRepository;

    @Mock
    protected SpringDataDepartmentRepository departmentRepository;

    @InjectMocks
    protected TimeSheetService timeSheetService;

    protected Department department(Long id) {
        Department department = new Department();
        department.setId(id);
        department.setName("IT");
        department.setCode("it-1");
        return department;
    }

    protected Employee employee(Long id, String pn, LocalDate hire, LocalDate dismissal) {
        return Employee.rehydrate(
                id,
                pn,
                "Иванов",
                "Иван",
                null,
                null,
                null,
                null,
                null,
                hire,
                dismissal,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                false
        );
    }

    protected TimeSheet timeSheet(Long id, int year, int month, boolean approved) {
        TimeSheet timeSheet = new TimeSheet();
        timeSheet.setId(id);
        timeSheet.setYear(year);
        timeSheet.setMonth(month);
        timeSheet.setDepartment(department(1L));
        timeSheet.setApproved(approved);
        timeSheet.setEntries(new ArrayList<>());
        return timeSheet;
    }

    protected TimeSheetEntry entry(Long id, TimeSheet ts, Employee e, int workedDays, int workedHours) {
        TimeSheetEntry entry = new TimeSheetEntry();
        entry.setId(id);
        entry.setTimeSheet(ts);
        entry.setEmployee(e);
        entry.setWorkedDays(workedDays);
        entry.setWorkedHours(workedHours);
        entry.setVacationDays(0);
        entry.setSickLeaveDays(0);
        entry.setBusinessTripDays(0);
        entry.setAbsentDays(0);
        entry.setOvertimeHours(0);
        entry.setNightHours(0);
        return entry;
    }

    protected User user(Long id, String username, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        return user;
    }

    protected CreateTimeSheetCommand createRequest(int year, int month, Long departmentId) {
        return new CreateTimeSheetCommand(year, month, departmentId);
    }

    protected TimeSheetEntryUpdateCommand updateRequest(Long id, Integer workedDays, Integer workedHours) {
        return new TimeSheetEntryUpdateCommand(
                id,
                workedDays,
                workedHours,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    protected TimeSheetEntryUpdateCommand updateRequestWithoutId(Integer workedDays, Integer workedHours) {
        return new TimeSheetEntryUpdateCommand(
                null,
                workedDays,
                workedHours,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

//    protected void stubCreateSuccess(Department department, List<Employee> employees) {
//        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
//        when(timeSheetRepository.findByDepartmentAndPeriod(department.getId(), 2026, 1)).thenReturn(Optional.empty());
//        when(employeeRepository.findActiveByDepartmentOnDate(any(), any())).thenReturn(employees);
//        when(timeSheetRepository.save(any(TimeSheet.class))).thenAnswer(inv -> {
//            TimeSheet timeSheet = inv.getArgument(0);
//            if (timeSheet.getId() == null) {
//                timeSheet.setId(1L);
//            }
//            return timeSheet;
//        });
//    }

    protected void assertTimeSheetResult(TimeSheetResult result, TimeSheet expected) {
        assertThat(result.id()).isEqualTo(expected.getId());
        assertThat(result.year()).isEqualTo(expected.getYear());
        assertThat(result.month()).isEqualTo(expected.getMonth());
        assertThat(result.approved()).isEqualTo(expected.isApproved());
    }
}