package com.migestion.analytics.application;

import com.migestion.shared.exception.BusinessRuleViolationException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

final class AnalyticsPeriodResolver {

    private static final String CUSTOM_PERIOD = "personalizado";

    private AnalyticsPeriodResolver() {
        // Utility class
    }

    static ResolvedPeriod resolve(String periodo, LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        String normalized = periodo == null || periodo.isBlank() ? "mes" : periodo.toLowerCase();

        return switch (normalized) {
            case "hoy" -> fromDates(today, today, normalized);
            case "ayer" -> {
                LocalDate yesterday = today.minusDays(1);
                yield fromDates(yesterday, yesterday, normalized);
            }
            case "semana" -> {
                LocalDate start = today.minusDays(6);
                yield fromDates(start, today, normalized);
            }
            case "mes" -> {
                LocalDate start = today.withDayOfMonth(1);
                yield fromDates(start, today, normalized);
            }
            case "trimestre" -> {
                int firstMonthOfQuarter = ((today.getMonthValue() - 1) / 3) * 3 + 1;
                LocalDate start = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
                yield fromDates(start, today, normalized);
            }
            case "año" -> {
                LocalDate start = LocalDate.of(today.getYear(), 1, 1);
                yield fromDates(start, today, normalized);
            }
            case CUSTOM_PERIOD -> {
                if (fechaInicio == null || fechaFin == null) {
                    throw new BusinessRuleViolationException("PERIODO_INVALIDO", "fechaInicio and fechaFin are required for periodo personalizado");
                }
                if (fechaInicio.isAfter(fechaFin)) {
                    throw new BusinessRuleViolationException("PERIODO_INVALIDO", "fechaInicio must be less than or equal to fechaFin");
                }
                yield fromDates(fechaInicio, fechaFin, normalized);
            }
            default -> throw new BusinessRuleViolationException("PERIODO_INVALIDO", "Unsupported periodo: " + normalized);
        };
    }

    private static ResolvedPeriod fromDates(LocalDate startDate, LocalDate endDate, String periodKey) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        Instant start = startDateTime.toInstant(ZoneOffset.UTC);
        Instant endExclusive = endDateTime.toInstant(ZoneOffset.UTC);
        long days = Math.max(1, endDate.toEpochDay() - startDate.toEpochDay() + 1);

        Instant previousStart = start.minusSeconds(days * 24 * 60 * 60);
        Instant previousEndExclusive = start;

        String label = buildLabel(startDate, endDate, periodKey);

        return new ResolvedPeriod(start, endExclusive, previousStart, previousEndExclusive, label);
    }

    private static String buildLabel(LocalDate startDate, LocalDate endDate, String periodKey) {
        return switch (periodKey) {
            case "hoy" -> "Hoy";
            case "ayer" -> "Ayer";
            case "semana" -> "Ultimos 7 dias";
            case "mes" -> startDate.with(TemporalAdjusters.lastDayOfMonth()).getMonth().name() + " " + startDate.getYear();
            case "trimestre" -> "Trimestre " + (((startDate.getMonthValue() - 1) / 3) + 1) + " " + startDate.getYear();
            case "año" -> "Ano " + startDate.getYear();
            default -> startDate + " to " + endDate;
        };
    }

    record ResolvedPeriod(
            Instant start,
            Instant endExclusive,
            Instant previousStart,
            Instant previousEndExclusive,
            String label
    ) {
    }
}