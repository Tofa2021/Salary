package com.example.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "time_sheets")
public class TimeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;
    private int month;

    @ManyToOne
    private Department department;

    @OneToMany(mappedBy = "timeSheet")
    private List<TimeSheetEntry> entries;

    private Boolean approved;
    private LocalDateTime approvedAt;
}
