package com.example.backend.payroll.service;

import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionPeriodCommand;
import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionRateCommand;
import com.example.backend.payroll.api.dto.employer.CreateEmployerContributionCommand;
import com.example.backend.payroll.api.dto.employer.EmployerContributionResult;
import com.example.backend.payroll.application.service.EmployerContributionService;
import com.example.backend.payroll.domain.employer.exception.EmployerContributionNotFoundException;
import com.example.backend.payroll.domain.employer.exception.InvalidEmployerContributionException;
import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.port.EmployerContributionRepository;
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

import static com.example.backend.payroll.service.PayrollMocks.period2024;
import static com.example.backend.payroll.service.PayrollMocks.period2025;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerContributionServiceTest {

    @Mock
    EmployerContributionRepository repository;
    @InjectMocks
    EmployerContributionService service;

    private static EmployerContribution existing(Long id, String code) {
        return EmployerContribution.rehydrate(id, code, Percentage.of(34), period2024());
    }

    @Nested
    class Create {

        @Test
        void saves_when_period_does_not_overlap() {
            when(repository.existsOverlapping(eq("SOCIAL"), any(), isNull())).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> {
                EmployerContribution c = inv.getArgument(0);
                return EmployerContribution.rehydrate(1L, c.getCode(), c.getRate(), c.getValidPeriod());
            });

            EmployerContributionResult result = service.create(
                    new CreateEmployerContributionCommand("SOCIAL", Percentage.of(34), period2024()));

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.code()).isEqualTo("SOCIAL");

            ArgumentCaptor<EmployerContribution> captor =
                    ArgumentCaptor.forClass(EmployerContribution.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getRate()).isEqualTo(Percentage.of(34));
        }

        @Test
        void rejects_overlap_without_saving() {
            when(repository.existsOverlapping(eq("SOCIAL"), any(), isNull())).thenReturn(true);

            assertThatThrownBy(() -> service.create(
                    new CreateEmployerContributionCommand("SOCIAL", Percentage.of(34), period2024())))
                    .isInstanceOf(InvalidEmployerContributionException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    class Read {

        @Test
        void getAllActiveOnDate_maps_each_item() {
            when(repository.findActiveOnDate(any(LocalDate.class)))
                    .thenReturn(List.of(existing(1L, "SOCIAL"), existing(2L, "PENSION")));

            List<EmployerContributionResult> result =
                    service.getAllActiveOnDate(LocalDate.of(2024, 6, 1));

            assertThat(result).extracting(EmployerContributionResult::code)
                    .containsExactly("SOCIAL", "PENSION");
        }

        @Test
        void getById_throws_when_missing() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(1L))
                    .isInstanceOf(EmployerContributionNotFoundException.class);
        }
    }

    @Nested
    class ChangePeriod {

        @Test
        void rejects_overlap_with_other_record() {
            EmployerContribution c = existing(1L, "SOCIAL");
            when(repository.findById(1L)).thenReturn(Optional.of(c));
            when(repository.existsOverlapping(eq("SOCIAL"), any(), eq(1L))).thenReturn(true);

            assertThatThrownBy(() -> service.changePeriod(1L,
                    new ChangeEmployerContributionPeriodCommand(period2025())))
                    .isInstanceOf(InvalidEmployerContributionException.class);

            verify(repository, never()).save(any());
        }

        @Test
        void saves_when_no_overlap() {
            EmployerContribution c = existing(1L, "SOCIAL");
            when(repository.findById(1L)).thenReturn(Optional.of(c));
            when(repository.existsOverlapping(eq("SOCIAL"), any(), eq(1L))).thenReturn(false);
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.changePeriod(1L,
                    new ChangeEmployerContributionPeriodCommand(period2025()));

            ArgumentCaptor<EmployerContribution> captor = ArgumentCaptor.forClass(EmployerContribution.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getValidPeriod()).isEqualTo(period2025());
        }
    }

    @Nested
    class ChangeRate {

        @Test
        void updates_rate_and_saves() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing(1L, "SOCIAL")));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.changeRate(1L, new ChangeEmployerContributionRateCommand(Percentage.of(35)));

            ArgumentCaptor<EmployerContribution> captor = ArgumentCaptor.forClass(EmployerContribution.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getRate()).isEqualTo(Percentage.of(35));
            assertThat(captor.getValue().getValidPeriod()).isEqualTo(period2024());
        }
    }

    @Nested
    class Delete {

        @Test
        void deletes_existing() {
            EmployerContribution c = existing(1L, "SOCIAL");
            when(repository.findById(1L)).thenReturn(Optional.of(c));

            service.delete(1L);

            verify(repository).delete(c);
        }

        @Test
        void throws_when_missing_without_deleting() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(1L))
                    .isInstanceOf(EmployerContributionNotFoundException.class);

            verify(repository, never()).delete(any());
        }
    }
}
