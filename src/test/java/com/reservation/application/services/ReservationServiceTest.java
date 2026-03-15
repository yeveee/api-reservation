package com.reservation.application.services;

import com.reservation.domain.aggregates.Reservation;
import com.reservation.domain.entities.Participant;
import com.reservation.domain.entities.Room;
import com.reservation.domain.repositories.ReservationRepository;
import com.reservation.domain.repositories.RoomRepository;
import com.reservation.domain.valueobjects.ReservationPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Room room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Participant> participants;

    @BeforeEach
    void setUp() {
        room = new Room("room-id", "Salle de Yoga", 20);
        startTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        endTime = startTime.plusHours(2);
        participants = new ArrayList<>(Arrays.asList(
            new Participant("Alice", "alice@test.com"),
            new Participant("Bob", "bob@test.com")
        ));
    }

    @Test
    void shouldCreateReservation() {
        when(roomRepository.findById("room-id")).thenReturn(Optional.of(room));
        when(reservationRepository.findActiveReservationsByRoomIdAndPeriod(
            eq("room-id"), any(ReservationPeriod.class)
        )).thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation reservation = reservationService.createReservation(
            "room-id", startTime, endTime, participants
        );

        assertNotNull(reservation);
        assertEquals(room, reservation.getRoom());
        assertEquals(2, reservation.getParticipants().size());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldThrowExceptionWhenRoomNotFound() {
        when(roomRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(
                "non-existent", startTime, endTime, participants
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenRoomIsAlreadyReserved() {
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        Reservation existingReservation = Reservation.create(room, period, participants);

        when(roomRepository.findById("room-id")).thenReturn(Optional.of(room));
        when(reservationRepository.findActiveReservationsByRoomIdAndPeriod(
            eq("room-id"), any(ReservationPeriod.class)
        )).thenReturn(Arrays.asList(existingReservation));

        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(
                "room-id", startTime, endTime, participants
            );
        });
    }

    @Test
    void shouldCheckRoomAvailability() {
        when(roomRepository.existsById("room-id")).thenReturn(true);
        when(reservationRepository.findActiveReservationsByRoomIdAndPeriod(
            eq("room-id"), any(ReservationPeriod.class)
        )).thenReturn(new ArrayList<>());

        boolean available = reservationService.isRoomAvailable("room-id", startTime, endTime);

        assertTrue(available);
    }

    @Test
    void shouldDetectRoomUnavailability() {
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        Reservation existingReservation = Reservation.create(room, period, participants);

        when(roomRepository.existsById("room-id")).thenReturn(true);
        when(reservationRepository.findActiveReservationsByRoomIdAndPeriod(
            eq("room-id"), any(ReservationPeriod.class)
        )).thenReturn(Arrays.asList(existingReservation));

        boolean available = reservationService.isRoomAvailable("room-id", startTime, endTime);

        assertFalse(available);
    }

    @Test
    void shouldAddParticipantToReservation() {
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        Reservation reservation = Reservation.create(room, period, participants);
        Participant newParticipant = new Participant("Charlie", "charlie@test.com");

        when(reservationRepository.findById("reservation-id")).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation updated = reservationService.addParticipant("reservation-id", newParticipant);

        assertEquals(3, updated.getParticipants().size());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void shouldRemoveParticipantFromReservation() {
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        Reservation reservation = Reservation.create(room, period, participants);
        String participantId = participants.get(0).getId();

        when(reservationRepository.findById("reservation-id")).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation updated = reservationService.removeParticipant("reservation-id", participantId);

        assertEquals(1, updated.getParticipants().size());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void shouldCancelReservation() {
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        Reservation reservation = Reservation.create(room, period, participants);

        when(reservationRepository.findById("reservation-id")).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation cancelled = reservationService.cancelReservation("reservation-id");

        assertEquals(Reservation.ReservationStatus.CANCELLED, cancelled.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void shouldGetAllReservations() {
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        Reservation reservation1 = Reservation.create(room, period, participants);
        Reservation reservation2 = Reservation.create(room, period, participants);

        when(reservationRepository.findAll()).thenReturn(Arrays.asList(reservation1, reservation2));

        List<Reservation> reservations = reservationService.getAllReservations();

        assertEquals(2, reservations.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void shouldGetReservationsByRoomId() {
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        Reservation reservation = Reservation.create(room, period, participants);

        when(reservationRepository.findActiveReservationsByRoomId("room-id"))
            .thenReturn(Arrays.asList(reservation));

        List<Reservation> reservations = reservationService.getReservationsByRoomId("room-id");

        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findActiveReservationsByRoomId("room-id");
    }
}
