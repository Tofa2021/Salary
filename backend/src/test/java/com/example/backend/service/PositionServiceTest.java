package com.example.backend.service;

import com.example.backend.payroll.api.TariffRateLookup;
import com.example.backend.payroll.exception.tariffrate.TariffRateNotFoundException;
import com.example.backend.position.Position;
import com.example.backend.position.PositionRepository;
import com.example.backend.position.PositionService;
import com.example.backend.position.api.dto.PositionCommand;
import com.example.backend.position.api.dto.PositionResult;
import com.example.backend.position.exception.InvalidPositionNameException;
import com.example.backend.position.exception.InvalidTariffCategoryException;
import com.example.backend.position.exception.InvalidTariffCoefficientException;
import com.example.backend.position.exception.PositionNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {
    @Mock
    private PositionRepository positionRepository;

    @Mock
    private TariffRateLookup tariffRateLookup;

    @InjectMocks
    private PositionService positionService;

    @Test
    void create_Success() {
        PositionCommand request = new PositionCommand(
                "Инженер",
                2,
                new BigDecimal("1.23"),
                ""
        );
        when(positionRepository.save(any())).thenAnswer(inv -> {
            Position position = inv.getArgument(0);
            position.setId(1L);
            return position;
        });

        PositionResult result = positionService.create(request);

        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.tariffCategory()).isEqualTo(request.tariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(request.tariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(request.qualificationRequirements());

        verify(positionRepository).save(any());
    }

    @Test
    void create_Category1_Success() {
        PositionCommand request = new PositionCommand(
                "Инженер",
                1,
                new BigDecimal("1.23"),
                ""
        );
        when(positionRepository.save(any())).thenAnswer(inv -> {
            Position position = inv.getArgument(0);
            position.setId(1L);
            return position;
        });

        PositionResult result = positionService.create(request);

        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.tariffCategory()).isEqualTo(request.tariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(request.tariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(request.qualificationRequirements());

        verify(positionRepository).save(any());
    }

    @Test
    void create_Category27_Success() {
        PositionCommand request = new PositionCommand(
                "Инженер",
                27,
                new BigDecimal("1.23"),
                ""
        );
        when(positionRepository.save(any())).thenAnswer(inv -> {
            Position position = inv.getArgument(0);
            position.setId(1L);
            return position;
        });

        PositionResult result = positionService.create(request);

        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.tariffCategory()).isEqualTo(request.tariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(request.tariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(request.qualificationRequirements());

        verify(positionRepository).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {-20, -5, -1, 0, 28, 30, 50})
    void create_InvalidTariffCategory_Throws(int tariffCategory) {
        PositionCommand request = new PositionCommand(
                "Инженер",
                tariffCategory,
                new BigDecimal("1.23"),
                ""
        );

        assertThatThrownBy(() -> positionService.create(request)).isInstanceOf(InvalidTariffCategoryException.class);

        verify(positionRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1.00", "0.00", "0.4", "0.99"})
    void create_InvalidTariffCoefficient_Throws(String coefficient) {
        PositionCommand request = new PositionCommand(
                "Инженер",
                3,
                new BigDecimal(coefficient),
                ""
        );

        assertThatThrownBy(() -> positionService.create(request)).isInstanceOf(InvalidTariffCoefficientException.class);

        verify(positionRepository, never()).save(any());
    }

    @Test
    void getAll_Success() {
        Position position1 = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        Position position2 = new Position(2L, "B", 3, new BigDecimal("1.32"), "");
        List<Position> positions = List.of(position1, position2);
        when(positionRepository.findAll()).thenReturn(positions);

        List<PositionResult> result = positionService.getAll();

        assertThat(result.size()).isEqualTo(positions.size());

        assertThat(result.getFirst().id()).isEqualTo(position1.getId());
        assertThat(result.getFirst().name()).isEqualTo(position1.getName());
        assertThat(result.getFirst().tariffCategory()).isEqualTo(position1.getTariffCategory());
        assertThat(result.getFirst().tariffCoefficient()).isEqualTo(position1.getTariffCoefficient());
        assertThat(result.getFirst().qualificationRequirements()).isEqualTo(position1.getQualificationRequirements());

        assertThat(result.get(1).id()).isEqualTo(position2.getId());
        assertThat(result.get(1).name()).isEqualTo(position2.getName());
        assertThat(result.get(1).tariffCategory()).isEqualTo(position2.getTariffCategory());
        assertThat(result.get(1).tariffCoefficient()).isEqualTo(position2.getTariffCoefficient());
        assertThat(result.get(1).qualificationRequirements()).isEqualTo(position2.getQualificationRequirements());
    }

    @Test
    void getAll_Empty() {
        when(positionRepository.findAll()).thenReturn(List.of());

        List<PositionResult> result = positionService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getById_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");

        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        PositionResult result = positionService.getById(1L);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(position.getQualificationRequirements());
    }

    @Test
    void getById_NotFound_Throws() {
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.getById(1L))
                .isInstanceOf(PositionNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void update_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand("B", 3, new BigDecimal("1.3"), "new requirements");
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(positionRequest.name());
        assertThat(result.tariffCoefficient()).isEqualTo(positionRequest.tariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(positionRequest.tariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(positionRepository).save(any());
    }

    @Test
    void update_NullRequestName_DoesNotUpdateFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand(null, 3, new BigDecimal("1.3"), "new requirements");
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCoefficient()).isEqualTo(positionRequest.tariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(positionRequest.tariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(positionRepository).save(any());
    }

    @Test
    void update_NullRequestTariffCoefficient_DoesNotUpdateFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand("B", 3, null, "new requirements");
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(positionRequest.name());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(positionRequest.tariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(positionRepository).save(any());
    }

    @Test
    void update_NullRequestTariffCategory_DoesNotUpdateFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand("B", null, new BigDecimal("1.32"), "new requirements");
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(positionRequest.name());
        assertThat(result.tariffCoefficient()).isEqualTo(positionRequest.tariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(positionRepository).save(any());
    }

    @Test
    void update_NullRequestQualificationRequirements_DoesNotUpdateFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand("B", 3, new BigDecimal("1.32"), null);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(positionRequest.name());
        assertThat(result.tariffCoefficient()).isEqualTo(positionRequest.tariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(positionRequest.tariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(position.getQualificationRequirements());
        verify(positionRepository).save(any());
    }

    @Test
    void update_BlankQualificationRequirements_UpdatesFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(null, null, null, "");
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(positionRepository).save(any());
    }

    @Test
    void update_NullRequest_DoesNotChangeAnything() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(null, null, null, null);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(position.getQualificationRequirements());
        verify(positionRepository).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {-50, -10, -1, 0, 28, 30, 50})
    void update_InvalidTariffCategory_Throws(int tariffCategory) {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(null, tariffCategory, null, null);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        assertThatThrownBy(() -> positionService.update(1L, positionRequest))
                .isInstanceOf(InvalidTariffCategoryException.class);

        verify(positionRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1.00", "0.00", "0.63"})
    void update_InvalidTariffCoefficient_Throws(String tariffCoefficient) {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(null, null, new BigDecimal(tariffCoefficient), null);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        assertThatThrownBy(() -> positionService.update(1L, positionRequest))
                .isInstanceOf(InvalidTariffCoefficientException.class);

        verify(positionRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "     ", "\n", "\t", "\t\n", " \n", " \n "})
    void update_InvalidNewName_Throws(String name) {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(name, null, null, null);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        assertThatThrownBy(() -> positionService.update(1L, positionRequest))
                .isInstanceOf(InvalidPositionNameException.class);

        verify(positionRepository, never()).save(any());
    }

    @Test
    void update_PositionNotFound_Throws() {
        PositionCommand positionRequest = new PositionCommand("B", null, null, null);
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.update(1L, positionRequest))
                .isInstanceOf(PositionNotFoundException.class)
                .hasMessageContaining("1");

        verify(positionRepository, never()).save(any());
    }

    @Test
    void calculateBaseSalary_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("2.48"), "Old requirements");
        LocalDate date = LocalDate.of(2025, 3, 15);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(tariffRateLookup.getBaseRateOnDate(date)).thenReturn(new BigDecimal("250.00"));

        BigDecimal result = positionService.calculateBaseSalary(1L, date);

        assertThat(result).isEqualByComparingTo("620.00");
    }

    @Test
    void calculateBaseSalary_CoefficientOne_Success() {
        Position position = new Position(1L, "A", 2, BigDecimal.ONE, "Old requirements");
        LocalDate date = LocalDate.of(2025, 3, 15);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(tariffRateLookup.getBaseRateOnDate(date)).thenReturn(new BigDecimal("250.00"));

        BigDecimal result = positionService.calculateBaseSalary(1L, date);

        assertThat(result).isEqualByComparingTo("250.00");
    }

    @Test
    void calculateBaseSalary_RoundsToTwoDecimals() {
        Position position = new Position(1L, "A", 2, new BigDecimal("2.333"), "Old requirements");
        LocalDate date = LocalDate.of(2025, 3, 15);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(tariffRateLookup.getBaseRateOnDate(date)).thenReturn(new BigDecimal("250.00"));

        BigDecimal result = positionService.calculateBaseSalary(1L, date);

        assertThat(result).isEqualByComparingTo("583.25");
        assertThat(result.scale()).isEqualTo(2);
    }

    @Test
    void calculateBaseSalary_DifferentDates_DifferentBaseRates() {
        Position position = new Position(1L, "A", 2, new BigDecimal("2.48"), "Old requirements");
        LocalDate jan = LocalDate.of(2025, 1, 15);
        LocalDate may = LocalDate.of(2025, 5, 15);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(tariffRateLookup.getBaseRateOnDate(jan)).thenReturn(new BigDecimal("250.00"));
        when(tariffRateLookup.getBaseRateOnDate(may)).thenReturn(new BigDecimal("270.00"));

        BigDecimal janSalary = positionService.calculateBaseSalary(1L, jan);
        BigDecimal maySalary = positionService.calculateBaseSalary(1L, may);

        assertThat(janSalary).isEqualByComparingTo("620.00");
        assertThat(maySalary).isEqualByComparingTo("669.60");
    }

    @Test
    void delete_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Requirements");
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        positionService.delete(1L);

        verify(positionRepository).delete(position);
    }

    @Test
    void calculateBaseSalary_PositionNotFound_Throws() {
        LocalDate date = LocalDate.now();
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.calculateBaseSalary(1L, date))
                .isInstanceOf(PositionNotFoundException.class);

        verify(tariffRateLookup, never()).getBaseRateOnDate(date);
    }

    @Test
    void calculateBaseSalary_noTariffRate_throws() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Requirements");
        LocalDate date = LocalDate.of(2020, 1, 1);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(tariffRateLookup.getBaseRateOnDate(date)).thenThrow(new TariffRateNotFoundException("No tariff rate effective date " + date));

        assertThatThrownBy(() -> positionService.calculateBaseSalary(1L, date))
                .isInstanceOf(TariffRateNotFoundException.class);
    }

    @Test
    void delete_NotFound_Throws() {
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.delete(1L))
                .isInstanceOf(PositionNotFoundException.class)
                .hasMessageContaining("1");

        verify(positionRepository, never()).delete(any());
    }

    @Test
    void findByIdOptional_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Requirements");
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        Optional<PositionResult> resultOptional = positionService.findByIdOptional(1L);

        assertThat(resultOptional).isNotEmpty();
        PositionResult result = resultOptional.get();
        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(position.getQualificationRequirements());
    }

    @Test
    void findByIdOptional_Empty_Success() {
        when(positionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PositionResult> resultOptional = positionService.findByIdOptional(99L);

        assertThat(resultOptional).isEmpty();
    }
}