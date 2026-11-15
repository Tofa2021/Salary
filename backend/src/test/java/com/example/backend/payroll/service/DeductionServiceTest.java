package com.example.backend.payroll.service;

import com.example.backend.employee.api.EmployeeLookup;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.payroll.api.dto.deduction.ChangeDeductionValueCommand;
import com.example.backend.payroll.api.dto.deduction.CreateDeductionCommand;
import com.example.backend.payroll.api.dto.deduction.DeductionResultDto;
import com.example.backend.payroll.application.service.DeductionService;
import com.example.backend.payroll.domain.deduction.exception.DeductionNotFoundException;
import com.example.backend.payroll.domain.deduction.exception.InvalidDeductionException;
import com.example.backend.payroll.domain.deduction.model.Base;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.model.DeductionType;
import com.example.backend.payroll.domain.deduction.port.DeductionRepository;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.backend.payroll.service.PayrollMocks.money;
import static com.example.backend.payroll.service.PayrollMocks.period2024;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductionServiceTest {

    @Mock
    DeductionRepository repository;
    @Mock
    EmployeeLookup employeeLookup;
    @InjectMocks
    DeductionService service;

    private static Deduction existingPercentageDeduction(Long id) {
        return Deduction.rehydrate(id, 42L, DeductionType.ALIMONY, "1/4",
                Percentage.of(25), null, Base.GROSS, period2024());
    }

    private void employeeExists(Long id) {
        when(employeeLookup.findById(id)).thenReturn(Optional.of(mock(EmployeeResult.class)));
    }

    @Nested
    class Create {

        @Test
        void saves_when_employee_exists() {
            employeeExists(42L);
            when(repository.save(any())).thenAnswer(inv -> {
                Deduction d = inv.getArgument(0);
                return Deduction.rehydrate(1L, d.getEmployeeId(), d.getType(), d.getDescription(),
                        d.getPercentage(), d.getFixedAmount(), d.getBase(), d.getValidPeriod());
            });

            DeductionResultDto result = service.create(new CreateDeductionCommand(
                    42L, DeductionType.ALIMONY, "1/4",
                    Percentage.of(25), null, Base.GROSS, period2024()));

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.employeeId()).isEqualTo(42L);

            ArgumentCaptor<Deduction> captor = ArgumentCaptor.forClass(Deduction.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getPercentage()).isEqualTo(Percentage.of(25));
            assertThat(captor.getValue().getBase()).isEqualTo(Base.GROSS);
        }

        @Test
        void rejects_unknown_employee_without_saving() {
            when(employeeLookup.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(new CreateDeductionCommand(
                    999L, DeductionType.ALIMONY, null, Percentage.of(25), null, Base.GROSS, period2024())))
                    .isInstanceOf(InvalidDeductionException.class)
                    .hasMessageContaining("employee not found");

            verify(repository, never()).save(any());
        }

        @Test
        void rejects_invalid_value_combination_without_saving() {
            employeeExists(42L);

            assertThatThrownBy(() -> service.create(new CreateDeductionCommand(
                    42L, DeductionType.ALIMONY, null,
                    Percentage.of(25), money("50"), Base.GROSS, period2024())))
                    .isInstanceOf(InvalidDeductionException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    class Read {

        @Test
        void getByEmployee_returns_mapped_results() {
            when(repository.findActiveByEmployeeOnDate(eq(42L), any(LocalDate.class)))
                    .thenReturn(List.of(existingPercentageDeduction(1L)));

            List<DeductionResultDto> result = service.getByEmployee(42L, LocalDate.of(2024, 6, 1));

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().type()).isEqualTo(DeductionType.ALIMONY);
        }

        @Test
        void getById_throws_when_missing() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(1L))
                    .isInstanceOf(DeductionNotFoundException.class);
        }
    }

    @Nested
    class ChangeValue {

        @Test
        void switches_to_fixed_and_saves() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(existingPercentageDeduction(1L)));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.changeValue(1L, new ChangeDeductionValueCommand(
                    DeductionType.UNION, "Профсоюз", null, money("50"), null));

            ArgumentCaptor<Deduction> captor = ArgumentCaptor.forClass(Deduction.class);
            verify(repository).save(captor.capture());
            Deduction saved = captor.getValue();
            assertThat(saved.getType()).isEqualTo(DeductionType.UNION);
            assertThat(saved.getFixedAmount()).isEqualTo(money("50"));
            assertThat(saved.getPercentage()).isNull();
            assertThat(saved.getBase()).isNull();
        }

        @Test
        void does_not_save_when_combination_is_invalid() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(existingPercentageDeduction(1L)));

            assertThatThrownBy(() -> service.changeValue(1L,
                    new ChangeDeductionValueCommand(
                            DeductionType.UNION, null, Percentage.of(10), money("50"), null)))
                    .isInstanceOf(InvalidDeductionException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void deletes_existing() {
            Deduction d = existingPercentageDeduction(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(d));

            service.delete(1L);

            verify(repository).delete(d);
        }
    }
}
