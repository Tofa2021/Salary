package com.example.backend.service.employmentcontract;

class EmploymentContractServiceUpdateTest extends BaseEmploymentContractServiceTest {

//    @Test
//    void update_allFields_updatesEverything() {
//        Employee employee = employee(1L, "T-001");
//        EmploymentContract contract = contract(10L, employee);   // baseSalary = 1000
//        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));
//
//        UpdateEmploymentContractRequest request = new UpdateEmploymentContractRequest(
//                BigDecimal.valueOf(2000), null, BigDecimal.valueOf(25), 20, null);
//
//        EmploymentContractResult result = contractService.update(10L, request);
//
//        assertThat(result.baseSalary()).isEqualByComparingTo("2000");
//        assertThat(result.bonusPercent()).isEqualByComparingTo("25");
//        assertThat(result.workHoursPerWeek()).isEqualTo(20);
//    }
//
//    @Test
//    void update_allNullFields_doesNotChangeAnything() {
//        Employee employee = employee(1L, "T-001");
//        EmploymentContract contract = contract(10L, employee);
//        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));
//
//        UpdateEmploymentContractRequest request = new UpdateEmploymentContractRequest(
//                null, null, null, null, null);
//
//        EmploymentContractResult result = contractService.update(10L, request);
//
//        assertThat(result.baseSalary()).isEqualByComparingTo("1000");
//        assertThat(result.workHoursPerWeek()).isEqualTo(40);
//    }
//
//    @Test
//    void update_onlySalaryChanged() {
//        Employee employee = employee(1L, "T-001");
//        EmploymentContract contract = contract(10L, employee);
//        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));
//
//        UpdateEmploymentContractRequest request = new UpdateEmploymentContractRequest(
//                BigDecimal.valueOf(2500), null, null, null, null);
//
//        EmploymentContractResult result = contractService.update(10L, request);
//
//        assertThat(result.baseSalary()).isEqualByComparingTo("2500");
//        assertThat(result.workHoursPerWeek()).isEqualTo(40);
//    }
//
//    @Test
//    void update_invalidEndDate_throws() {
//        Employee employee = employee(1L, "T-001");
//        EmploymentContract contract = contract(10L, employee);   // startDate = 2026-01-01
//        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));
//
//        UpdateEmploymentContractRequest request = new UpdateEmploymentContractRequest(
//                null, null, null, null, LocalDate.of(2025, 12, 31));
//
//        assertThatThrownBy(() -> contractService.update(10L, request))
//                .isInstanceOf(InvalidEmploymentContractPeriodException.class);
//    }
//
//    @Test
//    void update_notFound_throws() {
//        when(contractRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> contractService.update(99L,
//                new UpdateEmploymentContractRequest(null, null, null, null, null)))
//                .isInstanceOf(EmploymentContractNotFoundException.class);
//    }
}