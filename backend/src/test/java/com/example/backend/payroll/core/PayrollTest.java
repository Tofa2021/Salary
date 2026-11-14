package com.example.backend.payroll.core;

import com.example.backend.payroll.domain.core.exception.InvalidPayrollStatusException;
import com.example.backend.payroll.domain.core.exception.PayrollAlreadyApprovedException;
import com.example.backend.payroll.domain.core.exception.PayrollCalculationException;
import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.model.PayrollStatus;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Period;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PayrollTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final Long EMPLOYEE_ID = 42L;
    private static final Long APPROVER_ID = 99L;
    private static final Period PERIOD = Period.of(2026, 10);
    private static final LocalDateTime CALC_TIME = LocalDateTime.of(2026, 11, 1, 10, 0);
    private static final LocalDateTime APPROVE_TIME = LocalDateTime.of(2026, 11, 2, 14, 30);

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    private static Payroll createDraft() {
        return Payroll.create(EMPLOYEE_ID, PERIOD);
    }

    private static Payroll createCalculated() {
        Payroll payroll = createDraft();
        payroll.applyCalculation(
                money("2000.00"),
                money("2500.00"),
                money("325.00"),
                money("25.00"),
                List.of(),
                money("2150.00"),
                money("850.00"),
                money("3350.00"),
                CALC_TIME
        );
        return payroll;
    }

    @Nested
    class Creation {

        @Test
        void createsDraft() {
            Payroll payroll = Payroll.create(EMPLOYEE_ID, PERIOD);

            assertThat(payroll.getId()).isNull();
            assertThat(payroll.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
            assertThat(payroll.getPeriod()).isEqualTo(PERIOD);
            assertThat(payroll.getStatus()).isEqualTo(PayrollStatus.DRAFT);
            assertThat(payroll.getDeductions()).isEmpty();
            assertThat(payroll.getCalculatedAt()).isNull();
            assertThat(payroll.getApprovedAt()).isNull();
            assertThat(payroll.getApprovedBy()).isNull();
        }

        @Test
        void rejectsNullArguments() {
            assertThatThrownBy(() -> Payroll.create(null, PERIOD))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Payroll.create(EMPLOYEE_ID, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rehydratesExistingPayroll() {
            DeductionResult deduction = mock(DeductionResult.class);
            List<DeductionResult> sourceList = new ArrayList<>(List.of(deduction));

            Payroll payroll = Payroll.rehydrate(
                    10L, EMPLOYEE_ID, PERIOD, PayrollStatus.APPROVED,
                    money("1000.00"), money("1200.00"), money("156.00"), money("12.00"),
                    sourceList, money("1032.00"), money("408.00"), money("1608.00"),
                    CALC_TIME, APPROVE_TIME, APPROVER_ID
            );

            sourceList.clear();

            assertThat(payroll.getId()).isEqualTo(10L);
            assertThat(payroll.getStatus()).isEqualTo(PayrollStatus.APPROVED);
            assertThat(payroll.getGross()).isEqualTo(money("1200.00"));
            assertThat(payroll.getNet()).isEqualTo(money("1032.00"));
            assertThat(payroll.getDeductions()).containsExactly(deduction);
            assertThat(payroll.getApprovedBy()).isEqualTo(APPROVER_ID);
            assertThat(payroll.getApprovedAt()).isEqualTo(APPROVE_TIME);
        }
    }

    @Nested
    class ApplyCalculation {

        @Test
        void appliesCalculationToDraft() {
            Payroll payroll = createDraft();
            DeductionResult deduction = mock(DeductionResult.class);
            List<DeductionResult> deductions = new ArrayList<>(List.of(deduction));

            payroll.applyCalculation(
                    money("2000.00"), money("2500.00"), money("325.00"), money("25.00"),
                    deductions, money("2150.00"), money("850.00"), money("3350.00"),
                    CALC_TIME
            );

            deductions.clear();

            assertThat(payroll.getStatus()).isEqualTo(PayrollStatus.CALCULATED);
            assertThat(payroll.getBase()).isEqualTo(money("2000.00"));
            assertThat(payroll.getGross()).isEqualTo(money("2500.00"));
            assertThat(payroll.getTax()).isEqualTo(money("325.00"));
            assertThat(payroll.getSocialEmployee()).isEqualTo(money("25.00"));
            assertThat(payroll.getDeductions()).containsExactly(deduction);
            assertThat(payroll.getNet()).isEqualTo(money("2150.00"));
            assertThat(payroll.getEmployerContributions()).isEqualTo(money("850.00"));
            assertThat(payroll.getEmployerCost()).isEqualTo(money("3350.00"));
            assertThat(payroll.getCalculatedAt()).isEqualTo(CALC_TIME);
        }

        @Test
        void allowsRecalculation() {
            Payroll payroll = createCalculated();
            LocalDateTime secondCalcTime = CALC_TIME.plusHours(2);

            payroll.applyCalculation(
                    money("2100.00"), money("2600.00"), money("338.00"), money("26.00"),
                    List.of(), money("2236.00"), money("884.00"), money("3484.00"),
                    secondCalcTime
            );

            assertThat(payroll.getStatus()).isEqualTo(PayrollStatus.CALCULATED);
            assertThat(payroll.getGross()).isEqualTo(money("2600.00"));
            assertThat(payroll.getNet()).isEqualTo(money("2236.00"));
            assertThat(payroll.getCalculatedAt()).isEqualTo(secondCalcTime);
        }

        @Test
        void allowsBoundaryNetValues() {
            Payroll zeroDeductionsPayroll = createDraft();
            zeroDeductionsPayroll.applyCalculation(
                    money("1000.00"), money("1000.00"), money("0.00"), money("0.00"),
                    List.of(), money("1000.00"), money("0.00"), money("1000.00"),
                    CALC_TIME
            );
            assertThat(zeroDeductionsPayroll.getNet()).isEqualTo(money("1000.00"));

            Payroll fullDeductionPayroll = createDraft();
            fullDeductionPayroll.applyCalculation(
                    money("1000.00"), money("1000.00"), money("1000.00"), money("0.00"),
                    List.of(), money("0.00"), money("0.00"), money("1000.00"),
                    CALC_TIME
            );
            assertThat(fullDeductionPayroll.getNet()).isEqualTo(money("0.00"));
        }

        @Test
        void rejectsNetExceedingGross() {
            Payroll payroll = createDraft();

            assertThatThrownBy(() -> payroll.applyCalculation(
                    money("1000.00"), money("1000.00"), money("100.00"), money("10.00"),
                    List.of(), money("1000.01"), money("300.00"), money("1300.00"),
                    CALC_TIME
            )).isInstanceOf(PayrollCalculationException.class);
        }

        @Test
        void rejectsNegativeNet() {
            Payroll payroll = createDraft();

            assertThatThrownBy(() -> payroll.applyCalculation(
                    money("1000.00"), money("1000.00"), money("130.00"), money("10.00"),
                    List.of(), money("-0.01"), money("300.00"), money("1300.00"),
                    CALC_TIME
            )).isInstanceOf(PayrollCalculationException.class);
        }

        @Test
        void rejectsNullRequiredArguments() {
            Payroll payroll = createDraft();

            assertThatThrownBy(() -> payroll.applyCalculation(
                    money("100.00"), null, money("10.00"), money("1.00"),
                    List.of(), money("89.00"), money("30.00"), money("130.00"), CALC_TIME
            )).isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> payroll.applyCalculation(
                    money("100.00"), money("100.00"), null, money("1.00"),
                    List.of(), money("89.00"), money("30.00"), money("130.00"), CALC_TIME
            )).isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> payroll.applyCalculation(
                    money("100.00"), money("100.00"), money("10.00"), null,
                    List.of(), money("89.00"), money("30.00"), money("130.00"), CALC_TIME
            )).isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> payroll.applyCalculation(
                    money("100.00"), money("100.00"), money("10.00"), money("1.00"),
                    List.of(), null, money("30.00"), money("130.00"), CALC_TIME
            )).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> payroll.applyCalculation(
                    money("100.00"), money("100.00"), money("10.00"), money("1.00"),
                    List.of(), money("89.00"), money("30.00"), money("130.00"), null
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void forbidsCalculationOnFinalizedStates() {
            Payroll approved = createCalculated();
            approved.approve(APPROVER_ID, APPROVE_TIME);
            assertThatThrownBy(() -> approved.applyCalculation(
                    money("100.00"), money("100.00"), money("10.00"), money("1.00"),
                    List.of(), money("89.00"), money("30.00"), money("130.00"), CALC_TIME
            )).isInstanceOf(PayrollAlreadyApprovedException.class);
            Payroll cancelled = createDraft();
            cancelled.cancel();
            assertThatThrownBy(() -> cancelled.applyCalculation(
                    money("100.00"), money("100.00"), money("10.00"), money("1.00"),
                    List.of(), money("89.00"), money("30.00"), money("130.00"), CALC_TIME
            )).isInstanceOf(InvalidPayrollStatusException.class);
        }
    }

    @Nested
    class Approval {
        @Test
        void approvesCalculatedPayroll() {
            Payroll payroll = createCalculated();
            payroll.approve(APPROVER_ID, APPROVE_TIME);
            assertThat(payroll.getStatus()).isEqualTo(PayrollStatus.APPROVED);
            assertThat(payroll.getApprovedBy()).isEqualTo(APPROVER_ID);
            assertThat(payroll.getApprovedAt()).isEqualTo(APPROVE_TIME);
        }

        @Test
        void rejectsApprovalFromNonCalculatedStatuses() {
            Payroll draft = createDraft();
            assertThatThrownBy(() -> draft.approve(APPROVER_ID, APPROVE_TIME))
                    .isInstanceOf(InvalidPayrollStatusException.class);
            Payroll approved = createCalculated();
            approved.approve(APPROVER_ID, APPROVE_TIME);
            assertThatThrownBy(() -> approved.approve(APPROVER_ID, APPROVE_TIME.plusMinutes(5)))
                    .isInstanceOf(InvalidPayrollStatusException.class);
            Payroll cancelled = createCalculated();
            cancelled.cancel();
            assertThatThrownBy(() -> cancelled.approve(APPROVER_ID, APPROVE_TIME))
                    .isInstanceOf(InvalidPayrollStatusException.class);
        }

        @Test
        void rejectsNullApprovalArguments() {
            Payroll payroll = createCalculated();
            assertThatThrownBy(() -> payroll.approve(null, APPROVE_TIME))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> payroll.approve(APPROVER_ID, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Cancellation {
        @Test
        void cancelsDraftAndCalculated() {
            Payroll draft = createDraft();
            draft.cancel();
            assertThat(draft.getStatus()).isEqualTo(PayrollStatus.CANCELLED);
            Payroll calculated = createCalculated();
            calculated.cancel();
            assertThat(calculated.getStatus()).isEqualTo(PayrollStatus.CANCELLED);
        }

        @Test
        void rejectsDoubleCancellation() {
            Payroll payroll = createDraft();
            payroll.cancel();
            assertThatThrownBy(payroll::cancel)
                    .isInstanceOf(InvalidPayrollStatusException.class);
        }

        @Test
        void rejectsCancellingApprovedPayroll() {
            Payroll payroll = createCalculated();
            payroll.approve(APPROVER_ID, APPROVE_TIME);
            assertThatThrownBy(payroll::cancel)
                    .isInstanceOf(PayrollAlreadyApprovedException.class);
        }
    }
}
