package com.reservation.domain.valueobjects;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object représentant une période de réservation.
 * Garantit l'invariant : la date de début doit être antérieure à la date de fin.
 * Immuable par conception (pattern DDD).
 */
public class ReservationPeriod {
    
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public ReservationPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        validatePeriod(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Valide que la période est cohérente.
     * Invariant métier : start < end
     */
    private void validatePeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Les dates de début et de fin ne peuvent pas être nulles");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException(
                "La date de début doit être antérieure à la date de fin"
            );
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                "La date de début ne peut pas être dans le passé"
            );
        }
    }

    /**
     * Vérifie si cette période chevauche une autre période.
     * Règle métier essentielle pour éviter les doubles réservations.
     */
    public boolean overlapsWith(ReservationPeriod other) {
        if (other == null) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && 
               this.endTime.isAfter(other.startTime);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationPeriod that = (ReservationPeriod) o;
        return Objects.equals(startTime, that.startTime) && 
               Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return "ReservationPeriod{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
