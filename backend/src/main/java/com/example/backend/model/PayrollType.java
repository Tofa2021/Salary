package com.example.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payroll_types")
public class PayrollType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    private String name;

    @Enumerated(EnumType.STRING)
    private PayrollTypeCategory category;

    private boolean taxable;
    private boolean subjectToFszn;
    private boolean subjectToBelgosstrakh;
}
