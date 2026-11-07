package com.example.backend.service.employmentcontract;

import com.example.backend.dto.employmentcontract.CreateEmploymentContractRequest;
import com.example.backend.dto.employmentcontract.EmploymentContractResult;
import com.example.backend.model.employee.ContractType;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.employee.EmploymentContract;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.EmploymentContractRepository;
import com.example.backend.service.EmploymentContractService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class BaseEmploymentContractServiceTest {

    @Mock
    protected EmploymentContractRepository contractRepository;
    @Mock
    protected EmployeeRepository employeeRepository;
    @InjectMocks
    protected EmploymentContractService contractService;

    protected Employee employee(Long id, String personnelNumber) {
        Employee e = new Employee();
        e.setId(id);
        e.setPersonnelNumber(personnelNumber);
        e.setSurname("Иванов");
        e.setName("Иван");
        e.setHireDate(LocalDate.of(2026, 1, 1));
        return e;
    }

    protected EmploymentContract contract(Long id, Employee employee) {
        EmploymentContract c = new EmploymentContract();
        c.setId(id);
        c.setEmployee(employee);
        c.setContractNumber("C-" + id);
        c.setStartDate(LocalDate.of(2026, 1, 1));
        c.setEndDate(null);
        c.setContractType(ContractType.INDEFINITE);
        c.setBaseSalary(BigDecimal.valueOf(1000));
        c.setBonusPercent(BigDecimal.valueOf(10));
        c.setWorkHoursPerWeek(40);
        c.setMainJob(true);
        return c;
    }

    protected EmploymentContract fixedTermContract(Long id, Employee employee, LocalDate endDate) {
        EmploymentContract c = contract(id, employee);
        c.setContractType(ContractType.FIXED_TERM);
        c.setEndDate(endDate);
        return c;
    }

    protected CreateEmploymentContractRequest createRequest(String number, String type, boolean mainJob) {
        return new CreateEmploymentContractRequest(
                number,
                LocalDate.of(2026, 1, 1),
                null,
                type,
                BigDecimal.valueOf(1000),
                null,
                BigDecimal.valueOf(10),
                40,
                mainJob
        );
    }

    protected CreateEmploymentContractRequest createRequest(String number, String type,
                                                            LocalDate start, LocalDate end,
                                                            boolean mainJob) {
        return new CreateEmploymentContractRequest(
                number, start, end, type,
                BigDecimal.valueOf(1000), null, BigDecimal.valueOf(10),
                40, mainJob
        );
    }

    protected void stubCreateBase(Employee employee) {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(contractRepository.existsByContractNumber(any())).thenReturn(false);
        when(contractRepository.save(any(EmploymentContract.class))).thenAnswer(inv -> {
            EmploymentContract c = inv.getArgument(0);
            if (c.getId() == null) c.setId(1L);
            return c;
        });
    }

    protected void stubMainJobCheck() {
        when(contractRepository.findActiveMainOnDate(any(), any())).thenReturn(Optional.empty());
    }

    protected void stubCreateSuccess(Employee employee) {
        stubCreateBase(employee);
        stubMainJobCheck();
    }

    protected void assertContractResult(EmploymentContractResult result, EmploymentContract expected) {
        assertThat(result.id()).isEqualTo(expected.getId());
        assertThat(result.contractNumber()).isEqualTo(expected.getContractNumber());
        assertThat(result.startDate()).isEqualTo(expected.getStartDate());
        assertThat(result.endDate()).isEqualTo(expected.getEndDate());
        assertThat(result.contractType()).isEqualTo(expected.getContractType());
        assertThat(result.baseSalary()).isEqualByComparingTo(expected.getBaseSalary());
        assertThat(result.workHoursPerWeek()).isEqualTo(expected.getWorkHoursPerWeek());
        assertThat(result.mainJob()).isEqualTo(expected.isMainJob());
    }
}
