package com.example.backend.payroll.service;

import com.example.backend.payroll.api.dto.worknorm.UpsertWorkNormCommand;
import com.example.backend.payroll.api.dto.worknorm.WorkNormResult;
import com.example.backend.payroll.application.service.WorkNormService;
import com.example.backend.payroll.domain.base.exception.WorkNormNotFoundException;
import com.example.backend.payroll.domain.base.model.WorkNorm;
import com.example.backend.payroll.domain.base.port.WorkNormRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkNormServiceTest {

    @Mock
    WorkNormRepository repository;
    @InjectMocks
    WorkNormService service;

    @Nested
    class Upsert {

        @Test
        void saves_norm_with_given_values() {
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            WorkNormResult result = service.upsert(new UpsertWorkNormCommand(2024, 5, new BigDecimal("168")));

            assertThat(result.year()).isEqualTo(2024);
            assertThat(result.month()).isEqualTo(5);
            assertThat(result.normHours()).isEqualByComparingTo("168");

            ArgumentCaptor<WorkNorm> captor = ArgumentCaptor.forClass(WorkNorm.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().normHours()).isEqualByComparingTo("168");
        }
    }

    @Nested
    class Read {

        @Test
        void returns_norm_when_found() {
            when(repository.findByYearAndMonth(2024, 5))
                    .thenReturn(Optional.of(WorkNorm.of(2024, 5, new BigDecimal("168"))));

            WorkNormResult result = service.getByYearAndMonth(2024, 5);

            assertThat(result.normHours()).isEqualByComparingTo("168");
        }

        @Test
        void throws_when_missing() {
            when(repository.findByYearAndMonth(2024, 5)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getByYearAndMonth(2024, 5))
                    .isInstanceOf(WorkNormNotFoundException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void deletes_existing() {
            WorkNorm norm = WorkNorm.of(2024, 5, new BigDecimal("168"));
            when(repository.findByYearAndMonth(2024, 5)).thenReturn(Optional.of(norm));

            service.delete(2024, 5);

            verify(repository).delete(norm);
        }

        @Test
        void throws_when_missing_without_deleting() {
            when(repository.findByYearAndMonth(2024, 5)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(2024, 5))
                    .isInstanceOf(WorkNormNotFoundException.class);

            verify(repository, never()).delete(any());
        }
    }
}
