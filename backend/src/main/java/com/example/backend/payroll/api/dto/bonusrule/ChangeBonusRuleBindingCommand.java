package com.example.backend.payroll.api.dto.bonusrule;

public record ChangeBonusRuleBindingCommand(
        Long positionId,
        Long employeeId
) {
    public ChangeBonusRuleBindingCommand {
        if (positionId == null && employeeId == null) {
            throw new IllegalArgumentException("positionId or employeeId is required");
        }
        if (positionId != null && employeeId != null) {
            throw new IllegalArgumentException("only one of positionId/employeeId");
        }
    }
}
