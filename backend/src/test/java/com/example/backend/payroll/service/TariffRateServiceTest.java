package com.example.backend.payroll.service;

import com.example.backend.payroll.api.dto.rate.CreateTariffRateCommand;
import com.example.backend.payroll.api.dto.rate.TariffRateResult;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRateAmountCommand;
import com.example.backend.payroll.application.service.TariffRateService;
import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.base.exception.TariffRateNotFoundException;
import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.base.port.TariffRateRepository;
import com.example.backend.payroll.domain.base.rate.RateCalculationStrategy;
import com.example.backend.payroll.domain.base.rate.RateCalculationStrategyRegistry;
import com.example.backend.payroll.domain.shared.model.RateType;
import com.example.backend.position.api.PositionLookup;
import com.example.backend.position.api.dto.PositionResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.example.backend.payroll.service.PayrollMocks.money;
import static com.example.backend.payroll.service.PayrollMocks.period2024;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffRateServiceTest {

    @Mock
    TariffRateRepository repository;
    @Mock
    RateCalculationStrategyRegistry registry;
    @Mock
    PositionLookup positionLookup;
    @InjectMocks
    TariffRateService service;

    private static TariffRate existing(Long id, Long positionId, RateType type) {
        return TariffRate.rehydrate(id, positionId, money("1000"), type, period2024(),
                mock(RateCalculationStrategy.class));
    }

    private void positionExists(Long id) {
        when(positionLookup.findById(id)).thenReturn(Optional.of(mock(PositionResult.class)));
    }

    @Nested
    class Create {

        @Test
        void saves_with_strategy_from_registry() {
            positionExists(7L);
            when(repository.existsOverlapping(eq(7L), any(), isNull())).thenReturn(false);
            RateCalculationStrategy strategy = mock(RateCalculationStrategy.class);
            when(registry.get(RateType.MONTHLY)).thenReturn(strategy);
            when(repository.save(any())).thenAnswer(inv -> {
                TariffRate r = inv.getArgument(0);
                return TariffRate.rehydrate(1L, r.getPositionId(), r.getAmount(),
                        r.getType(), r.getValidPeriod(), r.getStrategy());
            });

            TariffRateResult result = service.create(new CreateTariffRateCommand(
                    7L, money("1000").amount(), "BYN", RateType.MONTHLY, period2024()));

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.type()).isEqualTo(RateType.MONTHLY);

            ArgumentCaptor<TariffRate> captor = ArgumentCaptor.forClass(TariffRate.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getAmount()).isEqualTo(money("1000"));
            assertThat(captor.getValue().getStrategy()).isSameAs(strategy);
        }

        @Test
        void rejects_unknown_position_without_saving() {
            when(positionLookup.findById(7L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(new CreateTariffRateCommand(
                    7L, money("1000").amount(), "BYN", RateType.MONTHLY, period2024())))
                    .isInstanceOf(InvalidRateException.class)
                    .hasMessageContaining("position not found");

            verify(repository, never()).save(any());
        }

        @Test
        void rejects_overlap_without_saving() {
            positionExists(7L);
            when(repository.existsOverlapping(eq(7L), any(), isNull())).thenReturn(true);

            assertThatThrownBy(() -> service.create(new CreateTariffRateCommand(
                    7L, money("1000").amount(), "BYN", RateType.MONTHLY, period2024())))
                    .isInstanceOf(InvalidRateException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    class Read {

        @Test
        void getActual_returns_rate_for_date() {
            when(repository.findActiveByPositionOnDate(eq(7L), any(LocalDate.class)))
                    .thenReturn(Optional.of(existing(1L, 7L, RateType.MONTHLY)));

            TariffRateResult result = service.getActual(7L, LocalDate.of(2024, 6, 1));

            assertThat(result.positionId()).isEqualTo(7L);
        }

        @Test
        void getActual_throws_when_missing() {
            when(repository.findActiveByPositionOnDate(eq(7L), any(LocalDate.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getActual(7L, LocalDate.of(2024, 6, 1)))
                    .isInstanceOf(TariffRateNotFoundException.class);
        }
    }

    @Nested
    class ChangeAmount {

        @Test
        void updates_amount_and_saves() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing(1L, 7L, RateType.MONTHLY)));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.changeAmount(1L, new UpdateTariffRateAmountCommand(money("2000").amount(), "BYN"));

            ArgumentCaptor<TariffRate> captor = ArgumentCaptor.forClass(TariffRate.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getAmount()).isEqualTo(money("2000"));
            assertThat(captor.getValue().getPositionId()).isEqualTo(7L); // не тронут
        }
    }

    @Nested
    class Delete {

        @Test
        void deletes_existing() {
            TariffRate r = existing(1L, 7L, RateType.MONTHLY);
            when(repository.findById(1L)).thenReturn(Optional.of(r));

            service.delete(1L);

            verify(repository).delete(r);
        }
    }
}
