package com.example.backend.timesheet.domain.policy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NightShiftPolicy {
    private static final LocalTime NIGHT_START = LocalTime.of(22, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    private static List<Interval> getIntervals(LocalTime start, LocalTime end) {
        if (start == null) {
            throw new IllegalArgumentException("start is required");
        }
        if (end == null) {
            throw new IllegalArgumentException("end is required");
        }
        if (start.equals(end)) {
            throw new IllegalArgumentException("start == end");
        }

        boolean overnight = end.isBefore(start);

        List<Interval> intervals = new ArrayList<>();
        if (overnight) {
            intervals.add(new Interval(start, LocalTime.MAX));
            intervals.add(new Interval(LocalTime.MIN, end));
        } else {
            intervals.add(new Interval(start, end));
        }
        return intervals;
    }

    public boolean isNight(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("time is required");
        }
        return !time.isBefore(NIGHT_START) || time.isBefore(NIGHT_END);
    }

    public BigDecimal nightHours(LocalTime start, LocalTime end) {
        List<Interval> intervals = getIntervals(start, end);

        List<Interval> nightIntervals = List.of(
                new Interval(LocalTime.MIN, NIGHT_END),
                new Interval(NIGHT_START, LocalTime.MAX)
        );

        BigDecimal totalMinutes = BigDecimal.ZERO;
        for (Interval work : intervals) {
            for (Interval night : nightIntervals) {
                totalMinutes = totalMinutes.add(overlapMinutes(work, night));
            }
        }
        return totalMinutes.divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal overlapMinutes(Interval a, Interval b) {
        LocalTime start = a.start().isAfter(b.start()) ? a.start() : b.start();
        LocalTime end = a.end().isBefore(b.end()) ? a.end() : b.end();

        if (!start.isBefore(end)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(Duration.between(start, end).toMinutes());
    }

    private record Interval(LocalTime start, LocalTime end) {
    }
}
