package com.example.backend.payroll.domain.statutory.policy;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;

public class SocialContributionPolicy {
    public Money calculate(Money gross, StatutoryRate socialRate) {
        if (gross == null) {
            throw new IllegalArgumentException("gross is required");
        }
        if (socialRate == null) {
            throw new IllegalArgumentException("socialRate is required");
        }

        return gross.multiply(socialRate.getRate());
    }
}
