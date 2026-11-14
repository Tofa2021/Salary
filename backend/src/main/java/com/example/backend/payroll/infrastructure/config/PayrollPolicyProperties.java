package com.example.backend.payroll.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "payroll")
public class PayrollPolicyProperties {
    private Overtime overtime = new Overtime();
    private NightShift nightShift = new NightShift();
    private Holiday holiday = new Holiday();

    @Getter
    @Setter
    public static class Overtime {
        private BigDecimal firstTwoMultiplier;

        private BigDecimal subsequentMultiplier;
    }

    @Getter
    @Setter
    public static class NightShift {
        private BigDecimal nightRatePercent;
    }

    @Getter
    @Setter
    public static class Holiday {
        private BigDecimal multiplier;
    }
}
