package com.example.backend.service.deduction;

class TaxDeductionServiceCreateTest extends BaseTaxDeductionServiceTest {

//    @Test
//    void create_StandardSelf_Success() {
//        Employee employee = employee(1L);
//        stubCreateBase(employee);
//
//        CreateDeductionRequest request = createRequest(
//                "STANDARD_SELF",
//                BigDecimal.valueOf(200),
//                LocalDate.of(2026, 1, 1),
//                null
//        );
//
//        DeductionResult result = deductionService.create(1L, request);
//
//        assertThat(result.id()).isEqualTo(1L);
//        assertThat(result.type()).isEqualTo(DeductionType.STANDARD_SELF);
//        assertThat(result.amount()).isEqualByComparingTo("200");
//        assertThat(result.active()).isTrue();
//
//        verify(deductionRepository).save(any(TaxDeduction.class));
//    }
//
//    @Test
//    void create_Child_Success() {
//        Employee employee = employee(1L);
//        stubCreateBase(employee);
//        when(deductionRepository.existsByEmployeeIdAndTypeAndDocumentNumber(
//                eq(1L), eq(DeductionType.CHILD), eq("DOC-CHILD")))
//                .thenReturn(false);
//
//        CreateDeductionRequest request = createChildRequest("DOC-CHILD");
//        DeductionResult result = deductionService.create(1L, request);
//
//        assertThat(result.type()).isEqualTo(DeductionType.CHILD);
//        assertThat(result.documentNumber()).isEqualTo("DOC-CHILD");
//        verify(deductionRepository).save(any(TaxDeduction.class));
//    }
//
//    @Test
//    void create_ChildWithExpiredValidTo_Success() {
//        Employee employee = employee(1L);
//        stubCreateBase(employee);
//        when(deductionRepository.existsByEmployeeIdAndTypeAndDocumentNumber(any(), any(), any())).thenReturn(false);
//
//        CreateDeductionRequest request = createRequest(
//                "CHILD",
//                BigDecimal.valueOf(200),
//                LocalDate.of(2026, 1, 1),
//                LocalDate.of(2030, 12, 31)
//        );
//
//        DeductionResult result = deductionService.create(1L, request);
//
//        assertThat(result.validTo()).isEqualTo(LocalDate.of(2030, 12, 31));
//    }
//
//    @Test
//    void create_EmployeeNotFound_Throws() {
//        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> deductionService.create(99L,
//                createRequest("STANDARD_SELF", BigDecimal.valueOf(200), LocalDate.now(), null)))
//                .isInstanceOf(EmployeeNotFoundException.class);
//
//        verify(deductionRepository, never()).save(any());
//    }
//
//    @Test
//    void create_ValidToBeforeValidFrom_Throws() {
//        Employee employee = employee(1L);
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//
//        CreateDeductionRequest request = createRequest(
//                "STANDARD_SELF",
//                BigDecimal.valueOf(200),
//                LocalDate.of(2026, 6, 1),
//                LocalDate.of(2026, 1, 1)
//        );
//
//        assertThatThrownBy(() -> deductionService.create(1L, request))
//                .isInstanceOf(InvalidDeductionPeriodException.class);
//
//        verify(deductionRepository, never()).save(any());
//    }
//
//    @Test
//    void create_DuplicateChildDeduction_Throws() {
//        Employee employee = employee(1L);
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(deductionRepository.existsByEmployeeIdAndTypeAndDocumentNumber(
//                eq(1L), eq(DeductionType.CHILD), eq("DOC-CHILD"))).thenReturn(true);
//
//        assertThatThrownBy(() -> deductionService.create(1L, createChildRequest("DOC-CHILD")))
//                .isInstanceOf(DuplicateChildDeductionException.class);
//
//        verify(deductionRepository, never()).save(any());
//    }
//
//    @Test
//    void create_childWithoutDocumentNumber_doesNotCheckDuplicate() {
//        Employee employee = employee(1L);
//        stubCreateBase(employee);
//
//        CreateDeductionRequest request = new CreateDeductionRequest(
//                "CHILD",
//                BigDecimal.valueOf(200),
//                LocalDate.of(2026, 1, 1),
//                null,
//                null
//        );
//
//        deductionService.create(1L, request);
//
//        verify(deductionRepository, never())
//                .existsByEmployeeIdAndTypeAndDocumentNumber(any(), any(), any());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"INVALID", "", "  "})
//    void create_InvalidType_Throws(String type) {
//        Employee employee = employee(1L);
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//
//        assertThatThrownBy(() -> deductionService.create(1L,
//                createRequest(type, BigDecimal.valueOf(200), LocalDate.now(), null)))
//                .isInstanceOf(UnknownDeductionTypeException.class);
//
//        verify(deductionRepository, never()).save(any());
//    }
//
//    @ParameterizedTest
//    @NullSource
//    void create_NullType_Throws(String type) {
//        Employee employee = employee(1L);
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//
//        assertThatThrownBy(() -> deductionService.create(1L,
//                createRequest(type, BigDecimal.valueOf(200), LocalDate.now(), null)))
//                .isInstanceOf(UnknownDeductionTypeException.class);
//    }
}