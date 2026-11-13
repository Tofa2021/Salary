package com.example.backend.payroll.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TariffRateLookup {
    BigDecimal getBaseRateOnDate(LocalDate date);
}
