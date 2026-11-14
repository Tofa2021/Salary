package com.example.backend.timesheet.infrastructure.persistance.entity;

import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "time_sheet_entries")
public class TimeSheetEntryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "timesheet_id", nullable = false)
    private TimeSheetJpaEntity timeSheet;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal hours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSheetEntryType type;
}
