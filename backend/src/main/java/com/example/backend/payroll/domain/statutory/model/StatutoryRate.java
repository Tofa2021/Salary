package com.example.backend.payroll.domain.statutory.model;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.payroll.domain.statutory.exception.InvalidStatutoryRateException;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class StatutoryRate {
    private Long id;
    private String code;
    private Percentage rate;
    private Money threshold;
    private DateRange validPeriod;

    private StatutoryRate() {
    }

    public static StatutoryRate create(String code, Percentage rate, Money threshold, DateRange validPeriod) {
        if (code == null || code.isBlank()) {
            throw new InvalidStatutoryRateException("code is required");
        }
        if (rate == null) {
            throw new InvalidStatutoryRateException("rate is required");
        }
        if (validPeriod == null) {
            throw new InvalidStatutoryRateException("validPeriod is required");
        }
        if (threshold != null && threshold.isNegative()) {
            throw new InvalidStatutoryRateException("threshold >= 0");
        }

        StatutoryRate statutoryRate = new StatutoryRate();
        statutoryRate.code = code;
        statutoryRate.rate = rate;
        statutoryRate.threshold = threshold;
        statutoryRate.validPeriod = validPeriod;
        return statutoryRate;
    }

    public static StatutoryRate rehydrate(Long id, String code, Percentage rate, Money threshold, DateRange validPeriod) {
        StatutoryRate statutoryRate = new StatutoryRate();
        statutoryRate.id = id;
        statutoryRate.code = code;
        statutoryRate.rate = rate;
        statutoryRate.threshold = threshold;
        statutoryRate.validPeriod = validPeriod;
        return statutoryRate;
    }

    public boolean appliesTo(LocalDate date) {
        return validPeriod.contains(date);
    }

    public Money taxableBase(Money gross) {
        if (threshold == null) {
            return gross;
        }
        return gross.isGreaterThan(threshold) ? gross.subtract(threshold) : Money.zero(gross.currency());
    }

    public void changeRate(Percentage rate, Money threshold) {
    }

    public void changePeriod(DateRange dateRange) {

    }
}
