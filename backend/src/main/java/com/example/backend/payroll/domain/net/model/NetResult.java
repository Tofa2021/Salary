package com.example.backend.payroll.domain.net.model;

import com.example.backend.payroll.domain.shared.model.Money;

public record NetResult(
        Money totalWithholding,
        Money net
) {
    public NetResult {
        if (totalWithholding == null || net == null)
            throw new IllegalArgumentException("totalWithholding and net are required");
    }
}
