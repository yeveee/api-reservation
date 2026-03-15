package com.reservation.domain.aggregates;

import com.reservation.domain.entities.Participant;
import com.reservation.domain.entities.Room;
import com.reservation.domain.valueobjects.ReservationPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private Room room;
    private ReservationPeriod period;
    private List<Participant> participants;

    @BeforeEach
    void setUp() {
        room = new Room("Salle de Yoga", 20);
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime end = start.plusHours(2);
        period = new ReservationPeriod(start, end);
        participants = new ArrayList<>(Arrays.asList(
            new Participant("Alice", "alice@test.com"),
            new Participant("Bob", "bob@test.com")
        ));
    }

    @Test
    void shouldCreateValidReservation() {
        Reservation reservation = Reservation.create(room, period, participants);

        assertNotNull(reservation);
        assertNotNull(reservation.getId());
        assertEquals(room, reservation.getRoom());
        assertEquals(period, reservation.getPeriod());
        assertEquals(2, reservation.getParticipants().size());
        assertEquals(Reservation.ReservationStatus.ACTIVE, reservation.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRoomIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            Reservation.create(null, period, participants);
        });
    }

    @Test
    void shouldThrowExceptionWhenPeriodIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            Reservation.create(room, null, participants);
        });
    }

    @Test
    void shouldThrowExceptionWhenParticipantsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            Reservation.create(room, period, null);
        });
    }

    @Test
    void shouldThrowExceptionWhenTooManyParticipants() {
        Room smallRoom = new Room("Petite Salle", 2);
        List<Participant> tooMany = Arrays.asList(
            new Participant("P1", "p1@test.com"),
            new Participant("P2", "p2@test.com"),
            new Participant("P3", "p3@test.com")
        );

        assertThrows(IllegalStateException.class, () -> {
            Reservation.create(smallRoom, period, tooMany);
        });
    }

    @Test
    void shouldAddParticipant() {
        Reservation reservation = Reservation.create(room, period, participants);
        Participant newParticipant = new Participant("Charlie", "charlie@test.com");

        reservation.addParticipant(newParticipant);

        assertEquals(3, reservation.getParticipants().size());
        assertTrue(reservation.getParticipants().contains(newParticipant));
    }

    @Test
    void shouldThrowExceptionWhenAddingParticipantToFullReservation() {
        Room smallRoom = new Room("Petite Salle", 2);
        List<Participant> twoParticipants = Arrays.asList(
            new Participant("P1", "p1@test.com"),
            new Participant("P2", "p2@test.com")
        );
        Reservation reservation = Reservation.create(smallRoom, period, twoParticipants);

        Participant newParticipant = new Participant("P3", "p3@test.com");

        assertThrows(IllegalStateException.class, () -> {
            reservation.addParticipant(newParticipant);
        });
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateParticipant() {
        Reservation reservation = Reservation.create(room, period, participants);
        Participant duplicate = participants.get(0);

        assertThrows(IllegalStateException.class, () -> {
            reservation.addParticipant(duplicate);
        });
    }

    @Test
    void shouldRemoveParticipant() {
        Reservation reservation = Reservation.create(room, period, participants);
        Participant toRemove = participants.get(0);

        reservation.removeParticipant(toRemove.getId());

        assertEquals(1, reservation.getParticipants().size());
        assertFalse(reservation.getParticipants().contains(toRemove));
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentParticipant() {
        Reservation reservation = Reservation.create(room, period, participants);

        assertThrows(IllegalStateException.class, () -> {
            reservation.removeParticipant("non-existent-id");
        });
    }

    @Test
    void shouldCancelReservation() {
        Reservation reservation = Reservation.create(room, period, participants);

        reservation.cancel();

        assertEquals(Reservation.ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancellingAlreadyCancelledReservation() {
        Reservation reservation = Reservation.create(room, period, participants);
        reservation.cancel();

        assertThrows(IllegalStateException.class, () -> {
            reservation.cancel();
        });
    }

    @Test
    void shouldThrowExceptionWhenAddingParticipantToCancelledReservation() {
        Reservation reservation = Reservation.create(room, period, participants);
        reservation.cancel();

        Participant newParticipant = new Participant("Charlie", "charlie@test.com");

        assertThrows(IllegalStateException.class, () -> {
            reservation.addParticipant(newParticipant);
        });
    }

    @Test
    void shouldThrowExceptionWhenRemovingParticipantFromCancelledReservation() {
        Reservation reservation = Reservation.create(room, period, participants);
        String participantId = participants.get(0).getId();
        reservation.cancel();

        assertThrows(IllegalStateException.class, () -> {
            reservation.removeParticipant(participantId);
        });
    }

    @Test
    void shouldDetectIfReservationIsFull() {
        Room smallRoom = new Room("Petite Salle", 2);
        List<Participant> twoParticipants = Arrays.asList(
            new Participant("P1", "p1@test.com"),
            new Participant("P2", "p2@test.com")
        );
        Reservation reservation = Reservation.create(smallRoom, period, twoParticipants);

        assertTrue(reservation.isFull());
    }

    @Test
    void shouldDetectIfReservationIsNotFull() {
        Reservation reservation = Reservation.create(room, period, participants);

        assertFalse(reservation.isFull());
    }

    @Test
    void shouldDetectOverlappingPeriods() {
        Reservation reservation = Reservation.create(room, period, participants);

        LocalDateTime overlapStart = period.getStartTime().plusHours(1);
        LocalDateTime overlapEnd = period.getEndTime().plusHours(1);
        ReservationPeriod overlappingPeriod = new ReservationPeriod(overlapStart, overlapEnd);

        assertTrue(reservation.overlapsWith(overlappingPeriod));
    }

    @Test
    void shouldDetectNonOverlappingPeriods() {
        Reservation reservation = Reservation.create(room, period, participants);

        LocalDateTime futureStart = period.getEndTime().plusHours(1);
        LocalDateTime futureEnd = futureStart.plusHours(2);
        ReservationPeriod futurePeriod = new ReservationPeriod(futureStart, futureEnd);

        assertFalse(reservation.overlapsWith(futurePeriod));
    }

    @Test
    void shouldReconstituteReservation() {
        String id = "test-id";
        Reservation reservation = Reservation.reconstitute(
            id, room, period, participants, Reservation.ReservationStatus.ACTIVE
        );

        assertNotNull(reservation);
        assertEquals(id, reservation.getId());
        assertEquals(room, reservation.getRoom());
        assertEquals(Reservation.ReservationStatus.ACTIVE, reservation.getStatus());
    }

    @Test
    void shouldReconstituteCancelledReservation() {
        Reservation reservation = Reservation.reconstitute(
            "test-id", room, period, participants, Reservation.ReservationStatus.CANCELLED
        );

        assertEquals(Reservation.ReservationStatus.CANCELLED, reservation.getStatus());
    }
}
