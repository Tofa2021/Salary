package com.example.backend.employee.infrastructure.persistance;

import com.example.backend.employee.domain.model.ContractType;
import com.example.backend.employee.domain.model.EmploymentContract;
import com.example.backend.employee.domain.port.EmploymentContractRepository;
import com.example.backend.employee.infrastructure.persistance.model.EmploymentContractJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaEmployeeContractRepository implements EmploymentContractRepository {
    private final SpringDataEmploymentContractRepository springDataRepository;

    @Override
    public boolean existsByContractNumber(String contractNumber) {
        return springDataRepository.existsByContractNumber(contractNumber);
    }

    @Override
    public Optional<EmploymentContract> findActiveMainOnDate(Long employeeId, LocalDate localDate) {
        return springDataRepository.findActiveMainOnDate(employeeId, localDate).map(this::toDomain);
    }

    @Override
    public EmploymentContract save(EmploymentContract contract) {
        EmploymentContractJpaEntity saved = springDataRepository.save(toEntity(contract));
        return toDomain(saved);
    }

    @Override
    public List<EmploymentContract> findByEmployeeId(Long employeeId) {
        return springDataRepository.findByEmployeeId(employeeId).stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(EmploymentContract contract) {
        springDataRepository.delete(toEntity(contract));
    }

    @Override
    public List<EmploymentContract> findExpiring(ContractType contractType, LocalDate today, LocalDate to) {
        return springDataRepository.findExpiring(contractType, today, to).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<EmploymentContract> findById(Long id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<EmploymentContract> findOverlappingMainJob(Long employeeId, Long excludeContractId, LocalDate start, LocalDate end) {
        return springDataRepository.findOverlappingMainJobs(employeeId, excludeContractId, start, end).stream()
                .findFirst().map(this::toDomain);
    }

    private EmploymentContract toDomain(EmploymentContractJpaEntity entity) {
        return EmploymentContract.rehydrate(
                entity.getId(), entity.getEmployeeId(), entity.getContractNumber(),
                entity.getStartDate(), entity.getEndDate(), entity.getContractType(),
                entity.getBaseSalary(), entity.getHourlyRate(), entity.getBonusPercent(),
                entity.getWorkHoursPerWeek(), entity.isMainJob()
        );
    }

    private EmploymentContractJpaEntity toEntity(EmploymentContract contract) {
        EmploymentContractJpaEntity entity = new EmploymentContractJpaEntity();
        entity.setId(contract.getId());
        entity.setEmployeeId(contract.getEmployeeId());
        entity.setContractNumber(contract.getContractNumber());
        entity.setStartDate(contract.getStartDate());
        entity.setEndDate(contract.getEndDate());
        entity.setContractType(contract.getContractType());
        entity.setBaseSalary(contract.getBaseSalary());
        entity.setHourlyRate(contract.getHourlyRate());
        entity.setBonusPercent(contract.getBonusPercent());
        entity.setWorkHoursPerWeek(contract.getWorkHoursPerWeek());
        entity.setMainJob(contract.isMainJob());
        return entity;
    }
}
