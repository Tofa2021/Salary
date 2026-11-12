package com.example.backend.department;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);

    boolean existsByCode(String code);

    long countByParentId(Long parentId);
}
