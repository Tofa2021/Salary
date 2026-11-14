package com.example.backend.payroll.domain.shared.model;


import com.example.backend.payroll.domain.shared.exception.InvalidMoneyException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {
    private static final int INTERMEDIATE_SCALE = 6;
    private static final int CURRENCY_SCALE = 2;

    public Money {
        if (amount == null) {
            throw new InvalidMoneyException("amount is required");
        }
        if (currency == null) {
            throw new InvalidMoneyException("currency is required");
        }
        if (amount.scale() > 2) {
            throw new InvalidMoneyException("amount scale must be <= 2");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(Percentage percentage) {
        return new Money(amount.multiply(percentage.asFraction()).setScale(2, RoundingMode.HALF_UP), currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor).setScale(2, RoundingMode.HALF_UP), currency);
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount) < 0;
    }

    private void requireSameCurrency(Money other) {
        if (other == null) {
            throw new InvalidMoneyException("other is required");
        }
        if (!currency.equals(other.currency)) {
            throw new InvalidMoneyException("currency mismatch");
        }
    }

    public Money divide(BigDecimal divisor) {
        if (divisor == null) {
            throw new InvalidMoneyException("divisor is required");
        }
        if (divisor.signum() <= 0) {
            throw new InvalidMoneyException("divisor must be > 0");
        }
        BigDecimal result = amount.divide(divisor, 2, RoundingMode.HALF_UP);
        return new Money(result, currency);
    }

    public Money divide(int divisor) {
        if (divisor <= 0) {
            throw new InvalidMoneyException("divisor must be > 0");
        }
        return divide(BigDecimal.valueOf(divisor));
    }

    public BigDecimal ratioTo(Money other) {
        requireSameCurrency(other);
        if (other.amount.signum() == 0) {
            throw new InvalidMoneyException("other amount must be > 0");
        }
        return amount.divide(other.amount, INTERMEDIATE_SCALE, RoundingMode.HALF_UP);
    }
}
