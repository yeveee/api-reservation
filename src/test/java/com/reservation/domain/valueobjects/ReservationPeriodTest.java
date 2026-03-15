package com.reservation.domain.valueobjects;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationPeriodTest {

    @Test
    void shouldCreateValidPeriod() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        ReservationPeriod period = new ReservationPeriod(start, end);

        assertNotNull(period);
        assertEquals(start, period.getStartTime());
        assertEquals(end, period.getEndTime());
    }

    @Test
    void shouldThrowExceptionWhenStartIsNull() {
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationPeriod(null, end);
        });
    }

    @Test
    void shouldThrowExceptionWhenEndIsNull() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationPeriod(start, null);
        });
    }

    @Test
    void shouldThrowExceptionWhenStartIsAfterEnd() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationPeriod(start, end);
        });
    }

    @Test
    void shouldThrowExceptionWhenStartEqualsEnd() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationPeriod(time, time);
        });
    }

    @Test
    void shouldThrowExceptionWhenStartIsInPast() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationPeriod(start, end);
        });
    }

    @Test
    void shouldDetectOverlappingPeriods() {
        LocalDateTime base = LocalDateTime.now().plusDays(1);
        
        // Période 1 : 10h-12h
        ReservationPeriod period1 = new ReservationPeriod(
            base.withHour(10).withMinute(0),
            base.withHour(12).withMinute(0)
        );

        // Période 2 : 11h-13h (chevauche période 1)
        ReservationPeriod period2 = new ReservationPeriod(
            base.withHour(11).withMinute(0),
            base.withHour(13).withMinute(0)
        );

        assertTrue(period1.overlapsWith(period2));
        assertTrue(period2.overlapsWith(period1));
    }

    @Test
    void shouldDetectNonOverlappingPeriods() {
        LocalDateTime base = LocalDateTime.now().plusDays(1);
        
        // Période 1 : 10h-12h
        ReservationPeriod period1 = new ReservationPeriod(
            base.withHour(10).withMinute(0),
            base.withHour(12).withMinute(0)
        );

        // Période 2 : 13h-15h (ne chevauche pas)
        ReservationPeriod period2 = new ReservationPeriod(
            base.withHour(13).withMinute(0),
            base.withHour(15).withMinute(0)
        );

        assertFalse(period1.overlapsWith(period2));
        assertFalse(period2.overlapsWith(period1));
    }

    @Test
    void shouldDetectAdjacentPeriodsAsNonOverlapping() {
        LocalDateTime base = LocalDateTime.now().plusDays(1);
        
        // Période 1 : 10h-12h
        ReservationPeriod period1 = new ReservationPeriod(
            base.withHour(10).withMinute(0),
            base.withHour(12).withMinute(0)
        );

        // Période 2 : 12h-14h (adjacente, ne chevauche pas)
        ReservationPeriod period2 = new ReservationPeriod(
            base.withHour(12).withMinute(0),
            base.withHour(14).withMinute(0)
        );

        assertFalse(period1.overlapsWith(period2));
        assertFalse(period2.overlapsWith(period1));
    }

    @Test
    void shouldDetectPeriodContainedInAnother() {
        LocalDateTime base = LocalDateTime.now().plusDays(1);
        
        // Période 1 : 10h-14h
        ReservationPeriod period1 = new ReservationPeriod(
            base.withHour(10).withMinute(0),
            base.withHour(14).withMinute(0)
        );

        // Période 2 : 11h-13h (contenue dans période 1)
        ReservationPeriod period2 = new ReservationPeriod(
            base.withHour(11).withMinute(0),
            base.withHour(13).withMinute(0)
        );

        assertTrue(period1.overlapsWith(period2));
        assertTrue(period2.overlapsWith(period1));
    }
}
