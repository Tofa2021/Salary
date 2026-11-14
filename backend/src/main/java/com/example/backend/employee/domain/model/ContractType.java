package com.example.backend.employee.domain.model;

import com.example.backend.employee.domain.exception.employmentcontract.UnknownEmploymentContractTypeException;

public enum ContractType {
    INDEFINITE,
    FIXED_TERM,
    CONTRACT,
    ;

    public static ContractType from(String value) {
        if (value == null) {
            throw new UnknownEmploymentContractTypeException(null);
        }

        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new UnknownEmploymentContractTypeException(value);
        }
    }
}
