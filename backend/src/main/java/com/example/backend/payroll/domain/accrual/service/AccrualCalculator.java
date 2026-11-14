package com.example.backend.payroll.domain.accrual.service;

import com.example.backend.payroll.domain.accrual.model.AccrualLine;
import com.example.backend.payroll.domain.accrual.model.AccrualResult;
import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.accrual.policy.BonusPolicy;
import com.example.backend.payroll.domain.accrual.policy.HolidayPolicy;
import com.example.backend.payroll.domain.accrual.policy.NightShiftPolicy;
import com.example.backend.payroll.domain.accrual.policy.OvertimePolicy;
import com.example.backend.payroll.domain.shared.model.HoursSummary;
import com.example.backend.payroll.domain.shared.model.Money;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AccrualCalculator {
    private final OvertimePolicy overtimePolicy;
    private final NightShiftPolicy nightPolicy;
    private final HolidayPolicy holidayPolicy;
    private final BonusPolicy bonusPolicy;

    public AccrualResult calculate(Money base, Money hourlyRate, HoursSummary hours, BonusRule bonusRule) {
        List<AccrualLine> lines = new ArrayList<>();
        Money total = Money.zero(base.currency());

        Money overtime = overtimePolicy.calculate(hourlyRate, hours.overtime());
        if (!overtime.isZero()) {
            lines.add(new AccrualLine("OVERTIME", "Переработка (" + hours.overtime() + " ч)", overtime));
            total = total.add(overtime);
        }

        Money night = nightPolicy.calculate(hourlyRate, hours.night());
        if (!night.isZero()) {
            lines.add(new AccrualLine("NIGHT", "Ночные (" + hours.night() + " ч)", night));
            total = total.add(night);
        }

        Money holiday = holidayPolicy.calculate(hourlyRate, hours.holiday());
        if (!holiday.isZero()) {
            lines.add(new AccrualLine("HOLIDAY", "Праздничные (" + hours.holiday() + " ч)", holiday));
            total = total.add(holiday);
        }

        Money grossWithoutBonus = base.add(total);
        Money bonus = bonusPolicy.calculate(base, grossWithoutBonus, bonusRule);
        if (!bonus.isZero()) {
            lines.add(new AccrualLine("BONUS", "Премия", bonus));
            total = total.add(bonus);
        }

        return AccrualResult.of(lines, total);
    }
}
