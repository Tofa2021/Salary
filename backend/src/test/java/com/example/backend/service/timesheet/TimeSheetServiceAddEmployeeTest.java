package com.example.backend.service.timesheet;

class TimeSheetServiceAddEmployeeTest extends BaseTimeSheetServiceTest {
//    @Test
//    void addEmployee_Valid_CreatesEntryWithZeros() {
//        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
//        Employee employee = employee(2L, "T-002", LocalDate.of(2026, 1, 15), null);
//        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
//        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
//        when(timeSheetEntryRepository.existsByTimeSheetIdAndEmployeeId(1L, 2L))
//                .thenReturn(false);
//        when(timeSheetEntryRepository.save(any())).thenAnswer(inv -> {
//            TimeSheetEntry entry = inv.getArgument(0);
//            entry.setId(10L);
//            return entry;
//        });
//
//        TimeSheetEntryResult result = timeSheetService.addEmployeeToTimeSheet(1L, 2L);
//
//        assertThat(result.id()).isEqualTo(10L);
//        assertThat(result.workedDays()).isZero();
//        verify(timeSheetEntryRepository).save(any());
//    }
//
//    @Test
//    void addEmployee_TimeSheetNotFound_Throws() {
//        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> timeSheetService.addEmployeeToTimeSheet(99L, 1L))
//                .isInstanceOf(TimeSheetNotFoundException.class);
//    }
//
//    @Test
//    void addEmployee_EmployeeNotFound_Throws() {
//        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
//        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
//        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> timeSheetService.addEmployeeToTimeSheet(1L, 99L))
//                .isInstanceOf(EmployeeNotFoundException.class);
//    }
//
//    @Test
//    void addEmployee_TimeSheetApproved_Throws() {
//        TimeSheet timeSheet = timeSheet(1L, 2026, 1, true);
//        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
//
//        assertThatThrownBy(() -> timeSheetService.addEmployeeToTimeSheet(1L, 1L))
//                .isInstanceOf(TimeSheetAlreadyApprovedException.class);
//    }
//
//    @Test
//    void addEmployee_Duplicate_Throws() {
//        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
//        Employee employee = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
//        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(timeSheetEntryRepository.existsByTimeSheetIdAndEmployeeId(1L, 1L))
//                .thenReturn(true);
//
//        assertThatThrownBy(() -> timeSheetService.addEmployeeToTimeSheet(1L, 1L))
//                .isInstanceOf(DuplicateTimeSheetEntryException.class);
//
//        verify(timeSheetEntryRepository, never()).save(any());
//    }
}
