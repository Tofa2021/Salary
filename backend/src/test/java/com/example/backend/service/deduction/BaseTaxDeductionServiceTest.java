package com.example.backend.service.deduction;

import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.infrastructure.repository.EmployeeRepository;
import com.example.backend.payroll.model.DeductionType;
import com.example.backend.payroll.model.TaxDeduction;
import com.example.backend.payroll.repository.TaxDeductionRepository;
import com.example.backend.payroll.service.TaxDeductionService;
import com.example.backend.shared.dto.deduction.CreateDeductionRequest;
import com.example.backend.shared.dto.deduction.DeductionResult;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class BaseTaxDeductionServiceTest {

    @Mock
    protected TaxDeductionRepository deductionRepository;

    @Mock
    protected EmployeeRepository employeeRepository;

    @InjectMocks
    protected TaxDeductionService deductionService;

    protected Employee employee(Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setPersonnelNumber("T-" + id);
        employee.setSurname("Иванов");
        employee.setName("Иван");
        return employee;
    }

    protected TaxDeduction deduction(
            Long id,
            Employee employee,
            DeductionType type,
            BigDecimal amount,
            LocalDate from,
            LocalDate to
    ) {
        TaxDeduction taxDeduction = new TaxDeduction();
        taxDeduction.setId(id);
        taxDeduction.setEmployee(employee);
        taxDeduction.setType(type);
        taxDeduction.setAmount(amount);
        taxDeduction.setValidFrom(from);
        taxDeduction.setValidTo(to);
        taxDeduction.setDocumentNumber("DOC-" + id);
        return taxDeduction;
    }

    protected TaxDeduction standardSelfDeduction(Long id, Employee employee) {
        return deduction(
                id,
                employee,
                DeductionType.STANDARD_SELF,
                BigDecimal.valueOf(200),
                LocalDate.of(2026, 1, 1),
                null
        );
    }

    protected TaxDeduction childDeduction(Long id, Employee employee, String documentNumber) {
        TaxDeduction deduction = deduction(
                id,
                employee,
                DeductionType.CHILD,
                BigDecimal.valueOf(200),
                LocalDate.of(2026, 1, 1),
                null
        );
        deduction.setDocumentNumber(documentNumber);
        return deduction;
    }

    protected CreateDeductionRequest createRequest(
            String type,
            BigDecimal amount,
            LocalDate from,
            LocalDate to
    ) {
        return new CreateDeductionRequest(type, amount, from, to, "DOC-001");
    }

    protected CreateDeductionRequest createChildRequest(String documentNumber) {
        return new CreateDeductionRequest(
                "CHILD",
                BigDecimal.valueOf(200),
                LocalDate.of(2026, 1, 1),
                null,
                documentNumber
        );
    }

    protected void stubCreateBase(Employee employee) {
        when(employeeRepository.findById(employee.getId())).thenReturn(java.util.Optional.of(employee));
        when(deductionRepository.save(any(TaxDeduction.class))).thenAnswer(inv -> {
            TaxDeduction deduction = inv.getArgument(0);

            if (deduction.getId() == null) {
                deduction.setId(1L);
            }

            return deduction;
        });
    }

    protected void assertDeductionResult(DeductionResult result, TaxDeduction expected) {
        assertThat(result.id()).isEqualTo(expected.getId());
        assertThat(result.employeeId()).isEqualTo(expected.getEmployee().getId());
        assertThat(result.type()).isEqualTo(expected.getType());
        assertThat(result.amount()).isEqualByComparingTo(expected.getAmount());
        assertThat(result.validFrom()).isEqualTo(expected.getValidFrom());
        assertThat(result.validTo()).isEqualTo(expected.getValidTo());
    }
}