package com.example.backend.payroll.domain.statutory.policy;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;

public class IncomeTaxPolicy {
    public Money calculate(Money gross, StatutoryRate statutoryRate) {
        if (gross == null) {
            throw new IllegalArgumentException("gross is required");
        }
        if (statutoryRate == null) {
            throw new IllegalArgumentException("statutoryRate is required");
        }

        Money base = statutoryRate.taxableBase(gross);
        return base.multiply(statutoryRate.getRate());
    }
}
