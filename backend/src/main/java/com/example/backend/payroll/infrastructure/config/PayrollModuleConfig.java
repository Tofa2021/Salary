package com.example.backend.payroll.infrastructure.config;

import com.example.backend.employee.api.EmployeeLookup;
import com.example.backend.payroll.application.service.BonusRuleService;
import com.example.backend.payroll.application.service.TariffRateService;
import com.example.backend.payroll.application.service.WorkNormService;
import com.example.backend.payroll.application.usecase.*;
import com.example.backend.payroll.domain.accrual.policy.BonusPolicy;
import com.example.backend.payroll.domain.accrual.policy.HolidayPolicy;
import com.example.backend.payroll.domain.accrual.policy.NightShiftPolicy;
import com.example.backend.payroll.domain.accrual.policy.OvertimePolicy;
import com.example.backend.payroll.domain.accrual.port.BonusRuleRepository;
import com.example.backend.payroll.domain.accrual.service.AccrualCalculator;
import com.example.backend.payroll.domain.base.port.TariffRateRepository;
import com.example.backend.payroll.domain.base.port.WorkNormRepository;
import com.example.backend.payroll.domain.base.rate.*;
import com.example.backend.payroll.domain.base.service.BaseSalaryCalculator;
import com.example.backend.payroll.domain.core.port.PayrollRepository;
import com.example.backend.payroll.domain.core.service.PayrollCalculator;
import com.example.backend.payroll.domain.deduction.policy.DeductionPolicy;
import com.example.backend.payroll.domain.deduction.port.DeductionRepository;
import com.example.backend.payroll.domain.deduction.service.DeductionCalculator;
import com.example.backend.payroll.domain.employer.policy.EmployerContributionPolicy;
import com.example.backend.payroll.domain.employer.port.EmployerContributionRepository;
import com.example.backend.payroll.domain.employer.service.EmployerCalculator;
import com.example.backend.payroll.domain.net.service.NetCalculator;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.payroll.domain.statutory.policy.IncomeTaxPolicy;
import com.example.backend.payroll.domain.statutory.policy.SocialContributionPolicy;
import com.example.backend.payroll.domain.statutory.port.StatutoryRateRepository;
import com.example.backend.payroll.domain.statutory.service.StatutoryCalculator;
import com.example.backend.position.api.PositionLookup;
import com.example.backend.timesheet.api.TimeSheetLookup;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration
@EnableConfigurationProperties(PayrollPolicyProperties.class)
public class PayrollModuleConfig {
    @Bean
    public IncomeTaxPolicy incomeTaxPolicy() {
        return new IncomeTaxPolicy();
    }

    @Bean
    public SocialContributionPolicy socialContributionPolicy() {
        return new SocialContributionPolicy();
    }

    @Bean
    public StatutoryCalculator statutoryCalculator(IncomeTaxPolicy incomeTaxPolicy, SocialContributionPolicy socialPolicy) {
        return new StatutoryCalculator(incomeTaxPolicy, socialPolicy);
    }

    @Bean
    public NetCalculator netCalculator() {
        return new NetCalculator();
    }

    @Bean
    public EmployerCalculator employerCalculator(EmployerContributionPolicy policy) {
        return new EmployerCalculator(policy);
    }

    @Bean
    public EmployerContributionPolicy employerContributionPolicy() {
        return new EmployerContributionPolicy();
    }

    @Bean
    public DeductionPolicy deductionPolicy() {
        return new DeductionPolicy();
    }

    @Bean
    public DeductionCalculator deductionCalculator(DeductionPolicy deductionPolicy) {
        return new DeductionCalculator(deductionPolicy);
    }

    @Bean
    public PayrollCalculator payrollCalculator(
            BaseSalaryCalculator baseCalculator,
            AccrualCalculator accrualCalculator,
            StatutoryCalculator statutoryCalculator,
            DeductionCalculator deductionCalculator,
            NetCalculator netCalculator,
            EmployerCalculator employerCalculator
    ) {
        return new PayrollCalculator(
                baseCalculator,
                accrualCalculator,
                statutoryCalculator,
                deductionCalculator,
                netCalculator,
                employerCalculator);
    }

    @Bean
    public BaseSalaryCalculator baseSalaryCalculator(RateCalculationStrategyRegistry registry) {
        return new BaseSalaryCalculator(registry);
    }

    @Bean
    public DailyRateStrategy dailyRateStrategy() {
        return new DailyRateStrategy();
    }

    @Bean
    public HourlyRateStrategy hourlyRateStrategy() {
        return new HourlyRateStrategy();
    }

    @Bean
    public MonthlyRateStrategy monthlyRateStrategy() {
        return new MonthlyRateStrategy();
    }

    @Bean
    public PieceworkRateStrategy pieceworkRateStrategy() {
        return new PieceworkRateStrategy();
    }

    @Bean
    public RateCalculationStrategyRegistry rateCalculationStrategyRegistry(
            DailyRateStrategy dailyRateStrategy,
            HourlyRateStrategy hourlyRateStrategy,
            MonthlyRateStrategy monthlyRateStrategy,
            PieceworkRateStrategy pieceworkRateStrategy
    ) {
        return new RateCalculationStrategyRegistry(List.of(
                dailyRateStrategy,
                hourlyRateStrategy,
                monthlyRateStrategy,
                pieceworkRateStrategy
        ));
    }

    @Bean
    public BonusPolicy bonusPolicy() {
        return new BonusPolicy();
    }

    @Bean
    public HolidayPolicy holidayPolicy(PayrollPolicyProperties props) {
        return new HolidayPolicy(props.getHoliday().getMultiplier());
    }

    @Bean
    public NightShiftPolicy payrollNightShiftPolicy(PayrollPolicyProperties props) {
        return new NightShiftPolicy(Percentage.of(props.getNightShift().getNightRatePercent()));
    }

    @Bean
    public OvertimePolicy payrollOvertimePolicy(PayrollPolicyProperties props) {
        return new OvertimePolicy(props.getOvertime().getFirstTwoMultiplier(), props.getOvertime().getSubsequentMultiplier());
    }

    @Bean
    public AccrualCalculator accrualCalculator(
            OvertimePolicy overtimePolicy,
            NightShiftPolicy nightPolicy,
            HolidayPolicy holidayPolicy,
            BonusPolicy bonusPolicy
    ) {
        return new AccrualCalculator(overtimePolicy, nightPolicy, holidayPolicy, bonusPolicy);
    }

    @Bean
    public ApprovePayrollUseCase approvePayrollUseCase(PayrollRepository payrollRepository, Clock clock) {
        return new ApprovePayrollUseCase(payrollRepository, clock);
    }

    @Bean
    public CalculatePayrollUseCase calculatePayrollUseCase(
            PayrollCalculator payrollCalculator,
            PayrollRepository payrollRepository,
            TariffRateRepository tariffRateRepository,
            WorkNormRepository workNormRepository,
            BonusRuleRepository bonusRuleRepository,
            StatutoryRateRepository statutoryRateRepository,
            DeductionRepository deductionRepository,
            EmployerContributionRepository employerContributionRepository,
            TimeSheetLookup timesheetLookup,
            EmployeeLookup employeeLookup,
            Clock clock
    ) {
        return new CalculatePayrollUseCase(
                payrollCalculator,
                payrollRepository,
                tariffRateRepository,
                workNormRepository,
                bonusRuleRepository,
                statutoryRateRepository,
                deductionRepository,
                employerContributionRepository,
                timesheetLookup,
                employeeLookup,
                clock
        );
    }

    @Bean
    public CancelPayrollUseCase cancelPayrollUseCase(PayrollRepository payrollRepository) {
        return new CancelPayrollUseCase(payrollRepository);
    }

    @Bean
    public GetPayrollUseCase getPayrollUseCase(PayrollRepository payrollRepository) {
        return new GetPayrollUseCase(payrollRepository);
    }

    @Bean
    public RecalculatePayrollUseCase recalculatePayrollUseCase(PayrollRepository payrollRepository, CalculatePayrollUseCase calculatePayroll) {
        return new RecalculatePayrollUseCase(payrollRepository, calculatePayroll);
    }

    @Bean
    public BonusRuleService bonusRuleService(BonusRuleRepository bonusRuleRepository) {
        return new BonusRuleService(bonusRuleRepository);
    }

    @Bean
    public TariffRateService tariffRateService(
            TariffRateRepository repo,
            RateCalculationStrategyRegistry registry,
            PositionLookup positionLookup
    ) {
        return new TariffRateService(repo, registry, positionLookup);
    }

    @Bean
    public WorkNormService workNormService(WorkNormRepository workNormRepository) {
        return new WorkNormService(workNormRepository);
    }
}
