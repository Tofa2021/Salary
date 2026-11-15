package com.example.backend.payroll.application.usecase;

import com.example.backend.employee.api.EmployeeLookup;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.payroll.api.dto.payroll.CalculatePayrollCommand;
import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.accrual.port.BonusRuleRepository;
import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.base.model.WorkNorm;
import com.example.backend.payroll.domain.base.port.TariffRateRepository;
import com.example.backend.payroll.domain.base.port.WorkNormRepository;
import com.example.backend.payroll.domain.core.exception.PayrollCalculationException;
import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.model.PayrollInput;
import com.example.backend.payroll.domain.core.port.PayrollRepository;
import com.example.backend.payroll.domain.core.service.PayrollCalculator;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.port.DeductionRepository;
import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.port.EmployerContributionRepository;
import com.example.backend.payroll.domain.shared.model.HoursSummary;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.port.StatutoryRateRepository;
import com.example.backend.timesheet.api.TimeSheetLookup;
import com.example.backend.timesheet.api.dto.TimeSheetSummaryResult;
import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

@RequiredArgsConstructor
public class CalculatePayrollUseCase {
    private final PayrollCalculator payrollCalculator;
    private final PayrollRepository payrollRepository;
    private final TariffRateRepository tariffRateRepository;
    private final WorkNormRepository workNormRepository;
    private final BonusRuleRepository bonusRuleRepository;
    private final StatutoryRateRepository statutoryRateRepository;
    private final DeductionRepository deductionRepository;
    private final EmployerContributionRepository employerContributionRepository;
    private final TimeSheetLookup timesheetLookup;
    private final EmployeeLookup employeeLookup;
    private final Clock clock;

    public Payroll execute(CalculatePayrollCommand command) {
        EmployeeResult employee = employeeLookup.findById(command.employeeId())
                .orElseThrow(() -> new PayrollCalculationException("employee not found: " + command.employeeId()));

        TariffRate rate = tariffRateRepository
                .findActiveByPositionOnDate(employee.positionId(), command.period().end())
                .orElseThrow(() -> new PayrollCalculationException("rate not found for position " + employee.positionId()));

        HoursSummary hours = toHoursSummary(timesheetLookup
                .findByEmployeeAndPeriod(command.employeeId(), command.period().start(), command.period().end())
                .orElseThrow(() -> new PayrollCalculationException("timesheet not found")));

        var normOpt = workNormRepository.findByYearAndMonth(command.period().year(), command.period().month());
        var normHours = normOpt.map(WorkNorm::normHours).orElse(null);
//
//        WorkVolumeSummary volume = workVolumeLookup // TODO volume and sales calculation
//                .findByEmployeeAndPeriod(command.employeeId(), command.period())
//                .orElse(null);
//
//        SalesSummary sales = salesLookup
//                .findByEmployeeAndPeriod(command.employeeId(), command.period())
//                .orElse(null);

        BonusRule bonusRule = bonusRuleRepository
                .findActiveByPositionOrEmployeeOnDate(employee.positionId(), command.employeeId(), command.period().end())
                .orElse(null);

        StatutoryRate incomeTaxRate = statutoryRateRepository
                .findActiveByCodeOnDate("INCOME_TAX", command.period().end())
                .orElseThrow(() -> new PayrollCalculationException("income tax rate not found"));
        StatutoryRate socialRate = statutoryRateRepository
                .findActiveByCodeOnDate("SOCIAL_EMPLOYEE", command.period().end())
                .orElseThrow(() -> new PayrollCalculationException("social rate not found"));

        List<Deduction> deductions = deductionRepository
                .findActiveByEmployeeOnDate(command.employeeId(), command.period().end());

        List<EmployerContribution> employerContributions = employerContributionRepository
                .findActiveOnDate(command.period().end());

        PayrollInput input = new PayrollInput(
                command.employeeId(), command.period(), hours, normHours, null, null,
                rate, bonusRule, incomeTaxRate, socialRate, deductions, employerContributions, clock
        );

        Payroll payroll = payrollCalculator.calculate(input);

        return payrollRepository.save(payroll);
    }

    private HoursSummary toHoursSummary(TimeSheetSummaryResult result) {
        BigDecimal regular = result.hoursByType().get(TimeSheetEntryType.REGULAR);
        BigDecimal overtime = result.hoursByType().get(TimeSheetEntryType.OVERTIME);
        BigDecimal night = result.hoursByType().get(TimeSheetEntryType.NIGHT);
        BigDecimal holiday = result.hoursByType().get(TimeSheetEntryType.HOLIDAY);
        return HoursSummary.of(regular, overtime, night, holiday);
    }
}