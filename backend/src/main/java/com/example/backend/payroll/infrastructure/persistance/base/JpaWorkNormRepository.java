package com.example.backend.payroll.infrastructure.persistance.base;

import com.example.backend.payroll.domain.base.model.WorkNorm;
import com.example.backend.payroll.domain.base.port.WorkNormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaWorkNormRepository implements WorkNormRepository {
    private final SpringDataWorkNormRepository springDataWorkNormRepository;

    @Override
    public Optional<WorkNorm> findByYearAndMonth(int year, int month) {
        return springDataWorkNormRepository.findByYearAndMonth(year, month).map(this::toDomain);
    }

    @Override
    public WorkNorm save(WorkNorm norm) {
        return toDomain(springDataWorkNormRepository.save(toEntity(norm)));
    }

    @Override
    public void delete(WorkNorm norm) {
        springDataWorkNormRepository.delete(toEntity(norm));
    }

    private WorkNorm toDomain(WorkNormJpaEntity entity) {
        return new WorkNorm(entity.getYear(), entity.getMonth(), entity.getNormHours());
    }

    private WorkNormJpaEntity toEntity(WorkNorm workNorm) {
        WorkNormJpaEntity entity = new WorkNormJpaEntity();
        entity.setYear(workNorm.year());
        entity.setMonth(workNorm.month());
        entity.setNormHours(workNorm.normHours());
        return entity;
    }
}
