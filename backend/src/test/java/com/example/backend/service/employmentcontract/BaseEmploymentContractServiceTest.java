package com.example.backend.service.employmentcontract;

import com.example.backend.employee.api.dto.CreateEmploymentContractCommand;
import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.application.service.EmploymentContractService;
import com.example.backend.employee.domain.model.*;
import com.example.backend.employee.domain.policy.MainJobPolicy;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.employee.domain.port.EmploymentContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
abstract class BaseEmploymentContractServiceTest {
    protected static final LocalDate TODAY = LocalDate.of(2026, 6, 1);

    @Mock
    protected EmploymentContractRepository contractRepository;

    @Mock
    protected EmployeeRepository employeeRepository;

    protected MainJobPolicy mainJobPolicy;
    protected Clock clock;
    protected EmploymentContractService contractService;

    @BeforeEach
    void setUpService() {
        mainJobPolicy = new MainJobPolicy();
        clock = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC);
        contractService = new EmploymentContractService(
                contractRepository, employeeRepository, mainJobPolicy, clock);
    }

    protected Employee employee(Long id, String personnelNumber) {
        return Employee.rehydrate(
                id, personnelNumber, "Иванов", "Иван", "Иванович",
                null, null, null, null,
                LocalDate.of(2026, 1, 1), null,
                "ivan@gmail.com", "+375251231231", Gender.MALE,
                1L, 1L, EmploymentType.FULL_TIME, 0, false
        );
    }

    protected EmploymentContract contract(Long id, Employee employee) {
        EmploymentContract contract = EmploymentContract.create(
                employee.getId(), "C-" + id,
                LocalDate.of(2026, 1, 1), null,
                ContractType.INDEFINITE,
                BigDecimal.valueOf(1000), null, BigDecimal.valueOf(10),
                40, true
        );


        return EmploymentContract.rehydrate(
                id, contract.getEmployeeId(), contract.getContractNumber(),
                contract.getStartDate(), contract.getEndDate(), contract.getContractType(),
                contract.getBaseSalary(), contract.getHourlyRate(), contract.getBonusPercent(),
                contract.getWorkHoursPerWeek(), contract.isMainJob()
        );
    }

    protected EmploymentContract fixedTermContract(Long id, Employee employee, LocalDate endDate) {
        EmploymentContract contract = EmploymentContract.create(
                employee.getId(), "C-" + id,
                LocalDate.of(2026, 1, 1), endDate,
                ContractType.FIXED_TERM,
                BigDecimal.valueOf(1000), null, BigDecimal.valueOf(10),
                40, true
        );
        return EmploymentContract.rehydrate(
                id, contract.getEmployeeId(), contract.getContractNumber(),
                contract.getStartDate(), contract.getEndDate(), contract.getContractType(),
                contract.getBaseSalary(), contract.getHourlyRate(), contract.getBonusPercent(),
                contract.getWorkHoursPerWeek(), contract.isMainJob()
        );
    }

    protected CreateEmploymentContractCommand createCommand(String number, String type, boolean mainJob) {
        return new CreateEmploymentContractCommand(
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

    protected CreateEmploymentContractCommand createCommand(
            String number,
            String type,
            LocalDate start,
            LocalDate end,
            boolean mainJob
    ) {
        return new CreateEmploymentContractCommand(
                number, start, end, type,
                BigDecimal.valueOf(1000), null, BigDecimal.valueOf(10),
                40, mainJob
        );
    }

    protected void stubCreateBase(Employee employee) {
        lenient().when(employeeRepository.existsById(employee.getId())).thenReturn(true);
        lenient().when(employeeRepository.findById(employee.getId())).thenReturn(java.util.Optional.of(employee));
        lenient().when(contractRepository.existsByContractNumber(any())).thenReturn(false);
        lenient().when(contractRepository.save(any(EmploymentContract.class))).thenAnswer(inv -> {
            EmploymentContract contract = inv.getArgument(0);
            if (contract.getId() != null) {
                return contract;
            }
            return EmploymentContract.rehydrate(
                    1L, contract.getEmployeeId(), contract.getContractNumber(),
                    contract.getStartDate(), contract.getEndDate(), contract.getContractType(),
                    contract.getBaseSalary(), contract.getHourlyRate(), contract.getBonusPercent(),
                    contract.getWorkHoursPerWeek(), contract.isMainJob()
            );
        });
    }

    protected void stubMainJobCheck() {
        lenient().when(contractRepository.findOverlappingMainJob(any(), any(), any(), any()))
                .thenReturn(java.util.Optional.empty());
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
