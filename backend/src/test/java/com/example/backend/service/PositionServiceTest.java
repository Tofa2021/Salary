package com.example.backend.service;

import com.example.backend.position.Position;
import com.example.backend.position.PositionService;
import com.example.backend.position.SpringDataPositionRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
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
    private SpringDataPositionRepository springDataPositionRepository;

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
        when(springDataPositionRepository.save(any())).thenAnswer(inv -> {
            Position position = inv.getArgument(0);
            position.setId(1L);
            return position;
        });

        PositionResult result = positionService.create(request);

        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.tariffCategory()).isEqualTo(request.tariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(request.tariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(request.qualificationRequirements());

        verify(springDataPositionRepository).save(any());
    }

    @Test
    void create_Category1_Success() {
        PositionCommand request = new PositionCommand(
                "Инженер",
                1,
                new BigDecimal("1.23"),
                ""
        );
        when(springDataPositionRepository.save(any())).thenAnswer(inv -> {
            Position position = inv.getArgument(0);
            position.setId(1L);
            return position;
        });

        PositionResult result = positionService.create(request);

        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.tariffCategory()).isEqualTo(request.tariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(request.tariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(request.qualificationRequirements());

        verify(springDataPositionRepository).save(any());
    }

    @Test
    void create_Category27_Success() {
        PositionCommand request = new PositionCommand(
                "Инженер",
                27,
                new BigDecimal("1.23"),
                ""
        );
        when(springDataPositionRepository.save(any())).thenAnswer(inv -> {
            Position position = inv.getArgument(0);
            position.setId(1L);
            return position;
        });

        PositionResult result = positionService.create(request);

        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.tariffCategory()).isEqualTo(request.tariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(request.tariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(request.qualificationRequirements());

        verify(springDataPositionRepository).save(any());
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

        verify(springDataPositionRepository, never()).save(any());
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

        verify(springDataPositionRepository, never()).save(any());
    }

    @Test
    void getAll_Success() {
        Position position1 = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        Position position2 = new Position(2L, "B", 3, new BigDecimal("1.32"), "");
        List<Position> positions = List.of(position1, position2);
        when(springDataPositionRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(positions));

        Page<PositionResult> result = positionService.getAll(0, 2);

        assertThat(result.getSize()).isEqualTo(positions.size());

        assertThat(result.getContent().getFirst().id()).isEqualTo(position1.getId());
        assertThat(result.getContent().getFirst().name()).isEqualTo(position1.getName());
        assertThat(result.getContent().getFirst().tariffCategory()).isEqualTo(position1.getTariffCategory());
        assertThat(result.getContent().getFirst().tariffCoefficient()).isEqualTo(position1.getTariffCoefficient());
        assertThat(result.getContent().getFirst().qualificationRequirements()).isEqualTo(position1.getQualificationRequirements());

        assertThat(result.getContent().get(1).id()).isEqualTo(position2.getId());
        assertThat(result.getContent().get(1).name()).isEqualTo(position2.getName());
        assertThat(result.getContent().get(1).tariffCategory()).isEqualTo(position2.getTariffCategory());
        assertThat(result.getContent().get(1).tariffCoefficient()).isEqualTo(position2.getTariffCoefficient());
        assertThat(result.getContent().get(1).qualificationRequirements()).isEqualTo(position2.getQualificationRequirements());
    }

    @Test
    void getAll_Empty() {
        when(springDataPositionRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

        Page<PositionResult> result = positionService.getAll(0, 2);

        assertTrue(result.isEmpty());
    }

    @Test
    void getById_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");

        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        PositionResult result = positionService.getById(1L);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.qualificationRequirements()).isEqualTo(position.getQualificationRequirements());
    }

    @Test
    void getById_NotFound_Throws() {
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.getById(1L))
                .isInstanceOf(PositionNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void update_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand("B", 3, new BigDecimal("1.3"), "new requirements");
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(springDataPositionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(positionRequest.name());
        assertThat(result.tariffCoefficient()).isEqualTo(positionRequest.tariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(positionRequest.tariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(springDataPositionRepository).save(any());
    }

    @Test
    void update_NullRequestName_DoesNotUpdateFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand(null, 3, new BigDecimal("1.3"), "new requirements");
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(springDataPositionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCoefficient()).isEqualTo(positionRequest.tariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(positionRequest.tariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(springDataPositionRepository).save(any());
    }

    @Test
    void update_NullRequestTariffCoefficient_DoesNotUpdateFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand("B", 3, null, "new requirements");
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(springDataPositionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(positionRequest.name());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(positionRequest.tariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(springDataPositionRepository).save(any());
    }

    @Test
    void update_NullRequestTariffCategory_DoesNotUpdateFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand("B", null, new BigDecimal("1.32"), "new requirements");
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(springDataPositionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(positionRequest.name());
        assertThat(result.tariffCoefficient()).isEqualTo(positionRequest.tariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(springDataPositionRepository).save(any());
    }

    @Test
    void update_NullRequestQualificationRequirements_DoesNotUpdateFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "");
        PositionCommand positionRequest = new PositionCommand("B", 3, new BigDecimal("1.32"), null);
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(springDataPositionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(positionRequest.name());
        assertThat(result.tariffCoefficient()).isEqualTo(positionRequest.tariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(positionRequest.tariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(position.getQualificationRequirements());
        verify(springDataPositionRepository).save(any());
    }

    @Test
    void update_BlankQualificationRequirements_UpdatesFiled() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(null, null, null, "");
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(springDataPositionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(positionRequest.qualificationRequirements());
        verify(springDataPositionRepository).save(any());
    }

    @Test
    void update_NullRequest_DoesNotChangeAnything() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(null, null, null, null);
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(springDataPositionRepository.save(position)).thenReturn(position);

        PositionResult result = positionService.update(1L, positionRequest);

        assertThat(result.id()).isEqualTo(position.getId());
        assertThat(result.name()).isEqualTo(position.getName());
        assertThat(result.tariffCoefficient()).isEqualTo(position.getTariffCoefficient());
        assertThat(result.tariffCategory()).isEqualTo(position.getTariffCategory());
        assertThat(result.qualificationRequirements()).isEqualTo(position.getQualificationRequirements());
        verify(springDataPositionRepository).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {-50, -10, -1, 0, 28, 30, 50})
    void update_InvalidTariffCategory_Throws(int tariffCategory) {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(null, tariffCategory, null, null);
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        assertThatThrownBy(() -> positionService.update(1L, positionRequest))
                .isInstanceOf(InvalidTariffCategoryException.class);

        verify(springDataPositionRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1.00", "0.00", "0.63"})
    void update_InvalidTariffCoefficient_Throws(String tariffCoefficient) {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(null, null, new BigDecimal(tariffCoefficient), null);
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        assertThatThrownBy(() -> positionService.update(1L, positionRequest))
                .isInstanceOf(InvalidTariffCoefficientException.class);

        verify(springDataPositionRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "     ", "\n", "\t", "\t\n", " \n", " \n "})
    void update_InvalidNewName_Throws(String name) {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Old requirements");
        PositionCommand positionRequest = new PositionCommand(name, null, null, null);
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        assertThatThrownBy(() -> positionService.update(1L, positionRequest))
                .isInstanceOf(InvalidPositionNameException.class);

        verify(springDataPositionRepository, never()).save(any());
    }

    @Test
    void update_PositionNotFound_Throws() {
        PositionCommand positionRequest = new PositionCommand("B", null, null, null);
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.update(1L, positionRequest))
                .isInstanceOf(PositionNotFoundException.class)
                .hasMessageContaining("1");

        verify(springDataPositionRepository, never()).save(any());
    }

    @Test
    void delete_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Requirements");
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));

        positionService.delete(1L);

        verify(springDataPositionRepository).delete(position);
    }

    @Test
    void delete_NotFound_Throws() {
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.delete(1L))
                .isInstanceOf(PositionNotFoundException.class)
                .hasMessageContaining("1");

        verify(springDataPositionRepository, never()).delete(any());
    }

    @Test
    void findByIdOptional_Success() {
        Position position = new Position(1L, "A", 2, new BigDecimal("1.23"), "Requirements");
        when(springDataPositionRepository.findById(1L)).thenReturn(Optional.of(position));

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
        when(springDataPositionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PositionResult> resultOptional = positionService.findByIdOptional(99L);

        assertThat(resultOptional).isEmpty();
    }
}