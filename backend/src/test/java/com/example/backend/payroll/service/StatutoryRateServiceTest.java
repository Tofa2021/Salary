package com.example.backend.payroll.service;

import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRatePeriodCommand;
import com.example.backend.payroll.api.dto.statutory.CreateStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.StatutoryRateResult;
import com.example.backend.payroll.application.service.StatutoryRateService;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.payroll.domain.statutory.exception.InvalidStatutoryRateException;
import com.example.backend.payroll.domain.statutory.exception.StatutoryRateNotFoundException;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.port.StatutoryRateRepository;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
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

import static com.example.backend.payroll.service.PayrollMocks.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatutoryRateServiceTest {

    @Mock
    StatutoryRateRepository repository;
    @InjectMocks
    StatutoryRateService service;

    private static StatutoryRate existing(Long id, String code) {
        return StatutoryRate.rehydrate(id, code, Percentage.of(13), null, period2024());
    }

    @Nested
    class Create {

        @Test
        void saves_when_period_does_not_overlap() {
            when(repository.existsOverlapping(eq("INCOME_TAX"), any(), isNull())).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> {
                StatutoryRate r = inv.getArgument(0);
                return StatutoryRate.rehydrate(1L, r.getCode(), r.getRate(), r.getThreshold(), r.getValidPeriod());
            });

            StatutoryRateResult result = service.create(
                    new CreateStatutoryRateCommand("INCOME_TAX", Percentage.of(13), null, period2024()));

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.rate()).isEqualTo(Percentage.of(13));
        }

        @Test
        void rejects_overlap_without_saving() {
            when(repository.existsOverlapping(eq("INCOME_TAX"), any(), isNull())).thenReturn(true);

            assertThatThrownBy(() -> service.create(
                    new CreateStatutoryRateCommand("INCOME_TAX", Percentage.of(13), null, period2024())))
                    .isInstanceOf(InvalidStatutoryRateException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    class Read {

        @Test
        void getActual_returns_rate_for_date() {
            when(repository.findActiveByCodeOnDate(eq("INCOME_TAX"), any(LocalDate.class)))
                    .thenReturn(Optional.of(existing(1L, "INCOME_TAX")));

            StatutoryRateResult result =
                    service.getActual("INCOME_TAX", LocalDate.of(2024, 6, 1));

            assertThat(result.code()).isEqualTo("INCOME_TAX");
        }

        @Test
        void getActual_throws_when_missing() {
            when(repository.findActiveByCodeOnDate(eq("INCOME_TAX"), any(LocalDate.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getActual("INCOME_TAX", LocalDate.of(2024, 6, 1)))
                    .isInstanceOf(StatutoryRateNotFoundException.class);
        }

        @Test
        void getAll_maps_items() {
            when(repository.findByCode(eq("INCOME_TAX"), any(PageRequest.class)))
                    .thenReturn(new PageResult<>(
                            List.of(existing(1L, "INCOME_TAX"), existing(2L, "INCOME_TAX")),
                            0, 10, 2));

            PageResult<StatutoryRateResult> page =
                    service.getAll("INCOME_TAX", new PageRequest(0, 10));

            assertThat(page.items()).hasSize(2);
        }
    }

    @Nested
    class ChangeRate {

        @Test
        void updates_rate_and_threshold_and_saves() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing(1L, "INCOME_TAX")));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.changeRate(1L, new ChangeStatutoryRateCommand(Percentage.of(15), money("100")));

            ArgumentCaptor<StatutoryRate> captor = ArgumentCaptor.forClass(StatutoryRate.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getRate()).isEqualTo(Percentage.of(15));
            assertThat(captor.getValue().getThreshold()).isEqualTo(money("100"));
        }

        @Test
        void rejects_invalid_rate_without_saving() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing(1L, "INCOME_TAX")));

            assertThatThrownBy(() -> service.changeRate(1L,
                    new ChangeStatutoryRateCommand(null, null)))
                    .isInstanceOf(InvalidStatutoryRateException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    class ChangePeriod {

        @Test
        void rejects_overlap_with_other_record() {
            StatutoryRate r = existing(1L, "INCOME_TAX");
            when(repository.findById(1L)).thenReturn(Optional.of(r));
            when(repository.existsOverlapping(eq("INCOME_TAX"), any(), eq(1L))).thenReturn(true);

            assertThatThrownBy(() -> service.changePeriod(1L,
                    new ChangeStatutoryRatePeriodCommand(period2025())))
                    .isInstanceOf(InvalidStatutoryRateException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void deletes_existing() {
            StatutoryRate r = existing(1L, "INCOME_TAX");
            when(repository.findById(1L)).thenReturn(Optional.of(r));

            service.delete(1L);

            verify(repository).delete(r);
        }

        @Test
        void throws_when_missing() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(1L))
                    .isInstanceOf(StatutoryRateNotFoundException.class);
        }
    }
}
