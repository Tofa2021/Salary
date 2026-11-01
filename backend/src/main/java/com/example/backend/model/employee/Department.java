package com.example.backend.model.employee;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;

    @ManyToOne
    private Department parent;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    public Department(String name, String code, Department parent, List<Employee> employees) {
        this.name = name;
        this.code = code;
        this.parent = parent;
        this.employees = employees;
    }
}
