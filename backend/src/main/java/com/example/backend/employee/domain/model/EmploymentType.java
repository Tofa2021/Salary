package com.example.backend.employee.domain.model;

import com.example.backend.employee.domain.exception.employee.UnknownEmploymentTypeException;

public enum EmploymentType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    ;

    public static EmploymentType from(String value) {
        if (value == null) {
            throw new UnknownEmploymentTypeException(null);
        }
        
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new UnknownEmploymentTypeException(value);
        }
    }
}
