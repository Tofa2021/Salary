package com.example.backend.payroll.infrastructure.web;

import com.example.backend.auth.api.CurrentUser;
import com.example.backend.payroll.api.PayrollApprovalFacade;
import com.example.backend.payroll.api.dto.ApprovePayrollCommand;
import com.example.backend.payroll.api.dto.CancelPayrollCommand;
import com.example.backend.payroll.api.dto.PayrollResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
public class PayrollApprovalController {
    private final PayrollApprovalFacade payrollApprovalFacade;

    @PostMapping("/{id}/approve")
    public ResponseEntity<PayrollResult> approve(@PathVariable Long id, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CurrentUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(payrollApprovalFacade.approve(new ApprovePayrollCommand(id, user.userId())));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PayrollResult> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(payrollApprovalFacade.cancel(new CancelPayrollCommand(id)));
    }
}
