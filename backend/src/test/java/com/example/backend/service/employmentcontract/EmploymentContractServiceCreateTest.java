package com.example.backend.service.employmentcontract;

class EmploymentContractServiceCreateTest extends BaseEmploymentContractServiceTest {

//    @Test
//    void create_Success() {
//        Employee employee = employee(1L, "T-001");
//        stubCreateSuccess(employee);
//
//        EmploymentContractResult result = contractService.create(1L, createRequest("C-001", "INDEFINITE", true));
//
//        assertThat(result.id()).isEqualTo(1L);
//        assertThat(result.contractNumber()).isEqualTo("C-001");
//        assertThat(result.contractType()).isEqualTo(ContractType.INDEFINITE);
//        assertThat(result.mainJob()).isTrue();
//        assertThat(result.active()).isTrue();
//
//        verify(contractRepository).existsByContractNumber("C-001");
//        verify(contractRepository).save(any(EmploymentContract.class));
//    }
//
//    @Test
//    void create_EmployeeNotFound_Throws() {
//        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> contractService.create(99L, createRequest("C-001", "INDEFINITE", true)))
//                .isInstanceOf(EmployeeNotFoundException.class)
//                .hasMessageContaining("99");
//
//        verify(contractRepository, never()).save(any());
//    }
//
//    @Test
//    void create_DuplicateContractNumber_Throws() {
//        Employee employee = employee(1L, "T-001");
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(contractRepository.existsByContractNumber("C-001")).thenReturn(true);
//
//        assertThatThrownBy(() -> contractService.create(1L, createRequest("C-001", "INDEFINITE", true)))
//                .isInstanceOf(EmploymentContractNumberAlreadyExistsException.class);
//
//        verify(contractRepository, never()).save(any());
//    }
//
//    @Test
//    void create_EndDateBeforeStartDate_Throws() {
//        Employee employee = employee(1L, "T-001");
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(contractRepository.existsByContractNumber(any())).thenReturn(false);
//
//        CreateEmploymentContractRequest request = createRequest(
//                "C-001", "FIXED_TERM",
//                LocalDate.of(2026, 6, 1),
//                LocalDate.of(2026, 1, 1),
//                true);
//
//        assertThatThrownBy(() -> contractService.create(1L, request))
//                .isInstanceOf(InvalidEmploymentContractPeriodException.class);
//
//        verify(contractRepository, never()).save(any());
//    }
//
//    @Test
//    void create_MainJobAlreadyExists_Throws() {
//        Employee employee = employee(1L, "T-001");
//        EmploymentContract existing = contract(10L, employee);
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(contractRepository.existsByContractNumber(any())).thenReturn(false);
//        when(contractRepository.findActiveMainOnDate(eq(1L), any())).thenReturn(Optional.of(existing));
//
//        assertThatThrownBy(() -> contractService.create(1L,
//                createRequest("C-001", "INDEFINITE", true)))
//                .isInstanceOf(MainJobAlreadyExistsException.class);
//
//        verify(contractRepository, never()).save(any());
//    }
//
//    @Test
//    void create_NotMainJob_DoesNotCheckExistingMain() {
//        Employee employee = employee(1L, "T-001");
//        stubCreateBase(employee);
//
//        contractService.create(1L, createRequest("C-001", "CONTRACT", false));
//
//        verify(contractRepository, never()).findActiveMainOnDate(any(), any());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"INVALID", "", "  "})
//    void create_InvalidContractType_Throws(String type) {
//        Employee employee = employee(1L, "T-001");
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(contractRepository.existsByContractNumber(any())).thenReturn(false);
//
//        assertThatThrownBy(() -> contractService.create(1L,
//                createRequest("C-001", type, true)))
//                .isInstanceOf(UnknownEmploymentContractTypeException.class);
//
//        verify(contractRepository, never()).save(any());
//    }
//
//    @ParameterizedTest
//    @NullSource
//    void create_NullContractType_Throws(String type) {
//        Employee employee = employee(1L, "T-001");
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(contractRepository.existsByContractNumber(any())).thenReturn(false);
//
//        assertThatThrownBy(() -> contractService.create(1L,
//                createRequest("C-001", type, true)))
//                .isInstanceOf(UnknownEmploymentContractTypeException.class);
//    }
}