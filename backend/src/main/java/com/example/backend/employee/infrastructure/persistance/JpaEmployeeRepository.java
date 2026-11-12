package com.example.backend.employee.infrastructure.persistance;

import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.employee.infrastructure.persistance.model.EmployeeJpaEntity;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaEmployeeRepository implements EmployeeRepository {
    private final SpringDataEmployeeRepository springDataEmployeeRepository;

    @Override
    public boolean existsByPersonnelNumber(String personnelNumber) {
        return springDataEmployeeRepository.existsByPersonnelNumber(personnelNumber);
    }

    @Override
    public Optional<Employee> findByPersonnelNumber(String personnelNumber) {
        return springDataEmployeeRepository.findByPersonnelNumber(personnelNumber).map(this::toDomain);
    }

    @Override
    public PageResult<Employee> findAll(PageRequest pageRequest) {
        Page<EmployeeJpaEntity> page = springDataEmployeeRepository.findAll(toPageable(pageRequest));
        return toPageResult(page);
    }

    @Override
    public PageResult<Employee> findByDepartmentId(Long departmentId, PageRequest pageRequest) {
        Page<EmployeeJpaEntity> page = springDataEmployeeRepository.findByDepartmentId(departmentId, toPageable(pageRequest));
        return toPageResult(page);
    }

    @Override
    public void delete(Employee employee) {
        springDataEmployeeRepository.delete(toEntity(employee));
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return springDataEmployeeRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeJpaEntity saved = springDataEmployeeRepository.save(toEntity(employee));
        return toDomain(saved);
    }

    @Override
    public boolean existsById(Long employeeId) {
        return springDataEmployeeRepository.existsById(employeeId);
    }

    private Pageable toPageable(PageRequest request) {
        return org.springframework.data.domain.PageRequest.of(request.page(), request.size());
    }

    private PageResult<Employee> toPageResult(Page<EmployeeJpaEntity> page) {
        return new PageResult<>(
                page.getContent().stream().map(this::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    private Employee toDomain(EmployeeJpaEntity jpaEntity) {
        return Employee.rehydrate(
                jpaEntity.getId(),
                jpaEntity.getPersonnelNumber(),
                jpaEntity.getSurname(),
                jpaEntity.getName(),
                jpaEntity.getMiddleName(),
                jpaEntity.getPassportSeries(),
                jpaEntity.getPassportNumber(),
                jpaEntity.getIdentificationNumber(),
                jpaEntity.getBirthDate(),
                jpaEntity.getHireDate(),
                jpaEntity.getDismissalDate(),
                jpaEntity.getEmail(),
                jpaEntity.getPhone(),
                jpaEntity.getGender(),
                jpaEntity.getPositionId(),
                jpaEntity.getDepartmentId(),
                jpaEntity.getEmploymentType(),
                jpaEntity.getDependentChildrenCount(),
                jpaEntity.isDisabled()
        );
    }

    private EmployeeJpaEntity toEntity(Employee employee) {
        EmployeeJpaEntity jpaEntity = new EmployeeJpaEntity();
        jpaEntity.setId(employee.getId());
        jpaEntity.setPersonnelNumber(employee.getPersonnelNumber());
        jpaEntity.setSurname(employee.getSurname());
        jpaEntity.setName(employee.getName());
        jpaEntity.setMiddleName(employee.getMiddleName());
        jpaEntity.setPassportSeries(employee.getPassportSeries());
        jpaEntity.setPassportNumber(employee.getPassportNumber());
        jpaEntity.setIdentificationNumber(employee.getIdentificationNumber());
        jpaEntity.setBirthDate(employee.getBirthDate());
        jpaEntity.setHireDate(employee.getHireDate());
        jpaEntity.setDismissalDate(employee.getDismissalDate());
        jpaEntity.setEmail(employee.getEmail());
        jpaEntity.setPhone(employee.getPhone());
        jpaEntity.setGender(employee.getGender());
        jpaEntity.setPositionId(employee.getPositionId());
        jpaEntity.setDepartmentId(employee.getDepartmentId());
        jpaEntity.setEmploymentType(employee.getEmploymentType());
        jpaEntity.setDependentChildrenCount(employee.getDependentChildrenCount());
        jpaEntity.setDisabled(employee.isDisabled());
        return jpaEntity;
    }
}
