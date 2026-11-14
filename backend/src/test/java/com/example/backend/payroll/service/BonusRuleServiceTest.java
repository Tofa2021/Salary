package com.example.backend.payroll.service;

import com.example.backend.payroll.api.dto.bonusrule.*;
import com.example.backend.payroll.application.service.BonusRuleService;
import com.example.backend.payroll.domain.accrual.exception.BonusRuleNotFoundException;
import com.example.backend.payroll.domain.accrual.exception.InvalidBonusRuleException;
import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.accrual.model.BonusType;
import com.example.backend.payroll.domain.accrual.port.BonusRuleRepository;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.backend.payroll.service.PayrollMocks.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BonusRuleServiceTest {

    @Mock
    BonusRuleRepository repository;

    @InjectMocks
    BonusRuleService service;

    private static BonusRule existingRule(Long id, BonusType type, Long employeeId, Long positionId) {
        return BonusRule.rehydrate(id, positionId, employeeId, type,
                type == BonusType.PERCENT_SALARY ? Percentage.of(10) : null,
                type == BonusType.FIXED ? money("500") : null,
                period2024());
    }

    private static BonusRuleResult resultOf(BonusRule rule) {
        return new BonusRuleResult(rule.getId(), rule.getPositionId(), rule.getEmployeeId(),
                rule.getType(), rule.getPercentage(), rule.getFixedAmount(), rule.getValidPeriod());
    }

    @Nested
    class Create {

        @Test
        void saves_rule_with_fields_from_command() {
            when(repository.save(any())).thenAnswer(inv -> {
                BonusRule rule = inv.getArgument(0);
                return BonusRule.rehydrate(1L, rule.getPositionId(), rule.getEmployeeId(),
                        rule.getType(), rule.getPercentage(), rule.getFixedAmount(), rule.getValidPeriod());
            });

            BonusRuleResult result = service.create(new CreateBonusRuleCommand(
                    null, 42L, BonusType.PERCENT_SALARY, Percentage.of(10), null, period2024()));

            ArgumentCaptor<BonusRule> captor = ArgumentCaptor.forClass(BonusRule.class);
            verify(repository).save(captor.capture());
            BonusRule saved = captor.getValue();

            assertThat(saved.getEmployeeId()).isEqualTo(42L);
            assertThat(saved.getType()).isEqualTo(BonusType.PERCENT_SALARY);
            assertThat(saved.getPercentage()).isEqualTo(Percentage.of(10));
            assertThat(saved.getValidPeriod()).isEqualTo(period2024());

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.employeeId()).isEqualTo(42L);
        }

        @Test
        void does_not_save_when_rule_is_invalid() {
            assertThatThrownBy(() -> service.create(new CreateBonusRuleCommand(
                    1L, 42L, BonusType.PERCENT_SALARY, Percentage.of(10), null, period2024())))
                    .isInstanceOf(InvalidBonusRuleException.class);

            verify(repository, never()).save(any());
        }

        @Test
        void rejects_fixed_rule_without_amount_without_hitting_repository() {
            assertThatThrownBy(() -> service.create(new CreateBonusRuleCommand(
                    null, 42L, BonusType.FIXED, null, null, period2024())))
                    .isInstanceOf(InvalidBonusRuleException.class);

            verifyNoInteractions(repository);
        }
    }

    @Nested
    class Read {

        @Test
        void getById_returns_mapped_result_when_found() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(existingRule(1L, BonusType.PERCENT_SALARY, 42L, null)));

            BonusRuleResult result = service.getById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.employeeId()).isEqualTo(42L);
        }

        @Test
        void getById_throws_when_missing() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(1L))
                    .isInstanceOf(BonusRuleNotFoundException.class);
        }

        @Test
        void getAll_maps_each_item() {
            BonusRule a = existingRule(1L, BonusType.PERCENT_SALARY, 42L, null);
            BonusRule b = existingRule(2L, BonusType.FIXED, null, 7L);
            when(repository.findAll(any(PageRequest.class)))
                    .thenReturn(new PageResult<>(List.of(a, b), 0, 10, 2));

            PageResult<BonusRuleResult> page = service.getAll(new PageRequest(0, 10));

            assertThat(page.items()).hasSize(2);
            assertThat(page.totalElements()).isEqualTo(2);
        }
    }

    @Nested
    class ChangeBinding {

        @Test
        void rebinds_to_position_and_saves() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(existingRule(1L, BonusType.PERCENT_SALARY, 42L, null)));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.changeBinding(1L, new ChangeBonusRuleBindingCommand(7L, null));

            ArgumentCaptor<BonusRule> captor = ArgumentCaptor.forClass(BonusRule.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getPositionId()).isEqualTo(7L);
            assertThat(captor.getValue().getEmployeeId()).isNull();
        }

        @Test
        void throws_when_id_missing() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.changeBinding(1L,
                    new ChangeBonusRuleBindingCommand(7L, null)))
                    .isInstanceOf(BonusRuleNotFoundException.class);
        }
    }

    @Nested
    class ChangeValue {

        @Test
        void switches_type_and_value_atomically() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(existingRule(1L, BonusType.PERCENT_SALARY, 42L, null)));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.changeValue(1L, new ChangeBonusRuleValueCommand(
                    BonusType.FIXED, null, money("300")));

            ArgumentCaptor<BonusRule> captor = ArgumentCaptor.forClass(BonusRule.class);
            verify(repository).save(captor.capture());
            BonusRule saved = captor.getValue();
            assertThat(saved.getType()).isEqualTo(BonusType.FIXED);
            assertThat(saved.getFixedAmount()).isEqualTo(money("300"));
            assertThat(saved.getPercentage()).isNull();
        }

        @Test
        void rejects_invalid_combination_without_saving() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(existingRule(1L, BonusType.PERCENT_SALARY, 42L, null)));

            assertThatThrownBy(() -> service.changeValue(1L,
                    new ChangeBonusRuleValueCommand(BonusType.FIXED, null, null)))
                    .isInstanceOf(InvalidBonusRuleException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    class ChangePeriod {

        @Test
        void updates_period_and_saves() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(existingRule(1L, BonusType.PERCENT_SALARY, 42L, null)));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.changePeriod(1L, new ChangeBonusRulePeriodCommand(period2025()));

            ArgumentCaptor<BonusRule> captor = ArgumentCaptor.forClass(BonusRule.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getValidPeriod()).isEqualTo(period2025());
            assertThat(captor.getValue().getPercentage()).isEqualTo(Percentage.of(10));
        }
    }

    @Nested
    class Delete {

        @Test
        void deletes_existing_rule() {
            BonusRule rule = existingRule(1L, BonusType.PERCENT_SALARY, 42L, null);
            when(repository.findById(1L)).thenReturn(Optional.of(rule));

            service.delete(1L);

            verify(repository).delete(rule);
        }

        @Test
        void throws_when_id_missing_without_deleting() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(1L))
                    .isInstanceOf(BonusRuleNotFoundException.class);

            verify(repository, never()).delete(any());
        }
    }
}
