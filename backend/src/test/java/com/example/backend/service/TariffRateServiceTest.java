package com.example.backend.service;

import com.example.backend.dto.tariffRate.CreateTariffRateRequest;
import com.example.backend.dto.tariffRate.TariffRateEffectiveResult;
import com.example.backend.dto.tariffRate.TariffRateResult;
import com.example.backend.dto.tariffRate.UpdateTariffRateRequest;
import com.example.backend.exception.TariffRateEffectiveFromAlreadyExistsException;
import com.example.backend.exception.notFound.TariffRateNotFoundException;
import com.example.backend.model.employee.TariffRate;
import com.example.backend.repository.TariffRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffRateServiceTest {
    @Mock
    private TariffRateRepository tariffRateRepository;

    @InjectMocks
    private TariffRateService tariffRateService;

    @Test
    void create_ClosesPreviousAndSavesNew() {
        TariffRate previous = new TariffRate(1L, new BigDecimal("250.00"), LocalDate.of(2025, 1, 1), null, "");

        LocalDate newFrom = LocalDate.of(2025, 5, 1);
        CreateTariffRateRequest request = new CreateTariffRateRequest(
                new BigDecimal("270.00"), newFrom, "Постановление № 456");
        when(tariffRateRepository.existsByEffectiveFrom(newFrom)).thenReturn(false);
        when(tariffRateRepository.findFirstByEffectiveToIsNullOrderByEffectiveFromDesc())
                .thenReturn(Optional.of(previous));
        when(tariffRateRepository.save(any())).thenAnswer(inv -> {
            TariffRate rate = inv.getArgument(0);
            rate.setId(2L);
            return rate;
        });

        TariffRateResult result = tariffRateService.create(request);

        assertThat(previous.getEffectiveTo()).isEqualTo(LocalDate.of(2025, 4, 30));
        assertThat(result.baseRate()).isEqualByComparingTo("270.00");
        assertThat(result.effectiveFrom()).isEqualTo(newFrom);
        assertThat(result.effectiveTo()).isNull();
        verify(tariffRateRepository).save(any(TariffRate.class));
    }

    @Test
    void create_noPrevious_savesFirstRate() {
        LocalDate newFrom = LocalDate.of(2025, 1, 1);
        when(tariffRateRepository.existsByEffectiveFrom(newFrom)).thenReturn(false);
        when(tariffRateRepository.findFirstByEffectiveToIsNullOrderByEffectiveFromDesc())
                .thenReturn(Optional.empty());
        when(tariffRateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        tariffRateService.create(new CreateTariffRateRequest(
                new BigDecimal("250.00"), newFrom, "act"));

        verify(tariffRateRepository).save(any(TariffRate.class));
    }

    @Test
    void create_duplicateEffectiveFrom_throws() {
        LocalDate date = LocalDate.of(2025, 5, 1);
        when(tariffRateRepository.existsByEffectiveFrom(date)).thenReturn(true);

        assertThatThrownBy(() -> tariffRateService.create(
                new CreateTariffRateRequest(new BigDecimal("270.00"), date, "act")))
                .isInstanceOf(TariffRateEffectiveFromAlreadyExistsException.class);

        verify(tariffRateRepository, never()).save(any());
        verify(tariffRateRepository, never()).findFirstByEffectiveToIsNullOrderByEffectiveFromDesc();
    }

    @Test
    void getEffective_returnsRate() {
        LocalDate date = LocalDate.of(2025, 3, 15);
        TariffRate rate = new TariffRate(
                1L,
                new BigDecimal("250.00"),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 4, 30),
                "Act"
        );

        when(tariffRateRepository.findEffectiveOn(date)).thenReturn(Optional.of(rate));

        TariffRateEffectiveResult result = tariffRateService.getEffective(date);

        assertThat(result.date()).isEqualTo(date);
        assertThat(result.baseRate()).isEqualByComparingTo("250.00");
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getEffective_noRate_throws() {
        LocalDate date = LocalDate.of(2020, 1, 1);
        when(tariffRateRepository.findEffectiveOn(date)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tariffRateService.getEffective(date))
                .isInstanceOf(TariffRateNotFoundException.class);
    }

    @Test
    void getEffective_nullDate_usesToday() {
        LocalDate today = LocalDate.now();
        TariffRate rate = new TariffRate(2L, new BigDecimal("270.00"), null, null, "");

        when(tariffRateRepository.findEffectiveOn(today)).thenReturn(Optional.of(rate));

        TariffRateEffectiveResult result = tariffRateService.getEffective(null);

        assertThat(result.date()).isEqualTo(today);
        assertThat(result.baseRate()).isEqualByComparingTo("270.00");
    }

    @Test
    void getBaseRateOn_returnsAmount() {
        LocalDate date = LocalDate.of(2025, 3, 15);
        TariffRate rate = new TariffRate(null, new BigDecimal("250.00"), null, null, null);

        when(tariffRateRepository.findEffectiveOn(date)).thenReturn(Optional.of(rate));

        assertThat(tariffRateService.getBaseRateOn(date)).isEqualByComparingTo("250.00");
    }

    @Test
    void update_changesLegalAct() {
        TariffRate rate = new TariffRate(1L, new BigDecimal("100.00"), LocalDate.of(2026, 1, 1), null, "old");
        when(tariffRateRepository.findById(1L)).thenReturn(Optional.of(rate));

        TariffRateResult result = tariffRateService.update(1L, new UpdateTariffRateRequest("new"));

        assertThat(result.legalAct()).isEqualTo("new");
    }

    @Test
    void update_notFound_throws() {
        when(tariffRateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tariffRateService.update(99L, new UpdateTariffRateRequest("x")))
                .isInstanceOf(TariffRateNotFoundException.class);
    }

    @Test
    void delete_notFound_throws() {
        when(tariffRateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tariffRateService.delete(99L))
                .isInstanceOf(TariffRateNotFoundException.class);
    }

    @Test
    void delete_existing_removes() {
        TariffRate rate = new TariffRate(1L, null, null, null, null);
        when(tariffRateRepository.findById(1L)).thenReturn(Optional.of(rate));

        tariffRateService.delete(1L);

        verify(tariffRateRepository).delete(rate);
    }
}