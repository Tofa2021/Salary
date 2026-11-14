package com.example.backend.auth.domain.model;

import java.util.Set;

public enum RolePermissions {
    ADMIN(Set.of(
            "user.manage",
            "payroll.calculate", "payroll.approve", "payroll.view",
            "employee.create", "employee.edit", "employee.view",
            "timesheet.approve", "timesheet.view",
            "rate.edit", "reports.view", "payslip.view.all"
    )),
    ACCOUNTANT(Set.of(
            "payroll.calculate", "payroll.view",
            "payslip.view.all", "reports.view"
    )),
    HR(Set.of(
            "employee.create", "employee.edit", "employee.view",
            "timesheet.approve", "timesheet.view",
            "payslip.view.all"
    )),
    ANALYST(Set.of(
            "payroll.view", "reports.view"
    )),
    VIEWER(Set.of(
            "payslip.view.own"
    ));

    private final Set<String> permissions;

    RolePermissions(Set<String> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    public static Set<String> of(UserRole role) {
        if (role == null) return Set.of();
        return RolePermissions.valueOf(role.name()).permissions();
    }

    public Set<String> permissions() {
        return permissions;
    }
}
