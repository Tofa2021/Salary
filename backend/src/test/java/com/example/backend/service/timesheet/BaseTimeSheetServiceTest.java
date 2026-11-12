package com.example.backend.service.timesheet;

import com.example.backend.auth.model.User;
import com.example.backend.auth.model.UserRole;
import com.example.backend.department.Department;
import com.example.backend.department.DepartmentRepository;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.infrastructure.persistance.SpringDataEmployeeRepository;
import com.example.backend.shared.dto.timesheet.CreateTimeSheetRequest;
import com.example.backend.shared.dto.timesheet.TimeSheetEntryUpdateRequest;
import com.example.backend.shared.dto.timesheet.TimeSheetResult;
import com.example.backend.timesheet.model.TimeSheet;
import com.example.backend.timesheet.model.TimeSheetEntry;
import com.example.backend.timesheet.repository.TimeSheetEntryRepository;
import com.example.backend.timesheet.repository.TimeSheetRepository;
import com.example.backend.timesheet.service.TimeSheetService;
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
    protected TimeSheetRepository timeSheetRepository;

    @Mock
    protected TimeSheetEntryRepository timeSheetEntryRepository;

    @Mock
    protected SpringDataEmployeeRepository employeeRepository;

    @Mock
    protected DepartmentRepository departmentRepository;

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
        Employee employee = new Employee();
        employee.setId(id);
        employee.setPersonnelNumber(pn);
        employee.setSurname("Иванов");
        employee.setName("Иван");
        employee.setHireDate(hire);
        employee.setDismissalDate(dismissal);
        return employee;
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

    protected CreateTimeSheetRequest createRequest(int year, int month, Long departmentId) {
        return new CreateTimeSheetRequest(year, month, departmentId);
    }

    protected TimeSheetEntryUpdateRequest updateRequest(Long id, Integer workedDays, Integer workedHours) {
        return new TimeSheetEntryUpdateRequest(
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

    protected TimeSheetEntryUpdateRequest updateRequestWithoutId(Integer workedDays, Integer workedHours) {
        return new TimeSheetEntryUpdateRequest(
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