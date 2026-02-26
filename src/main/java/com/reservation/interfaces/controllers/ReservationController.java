package com.reservation.interfaces.controllers;

import com.reservation.application.services.ReservationService;
import com.reservation.domain.aggregates.Reservation;
import com.reservation.domain.entities.Participant;
import com.reservation.interfaces.dto.CreateReservationRequest;
import com.reservation.interfaces.dto.ParticipantDTO;
import com.reservation.interfaces.dto.ReservationDTO;
import com.reservation.interfaces.mappers.DTOMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des réservations.
 * Couche Interfaces : expose les cas d'usage via HTTP.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
            @RequestBody CreateReservationRequest request) {
        try {
            List<Participant> participants = request.getParticipants().stream()
                .map(DTOMapper::toDomain)
                .collect(Collectors.toList());
            
            Reservation reservation = reservationService.createReservation(
                request.getRoomId(),
                request.getStartTime(),
                request.getEndTime(),
                participants
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(DTOMapper.toDTO(reservation));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable String id) {
        try {
            Reservation reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(DTOMapper.toDTO(reservation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        List<ReservationDTO> dtos = reservations.stream()
            .map(DTOMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ReservationDTO>> getActiveReservationsByRoom(
            @PathVariable String roomId) {
        try {
            List<Reservation> reservations = 
                reservationService.getActiveReservationsByRoom(roomId);
            List<ReservationDTO> dtos = reservations.stream()
                .map(DTOMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/room/{roomId}/availability")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @PathVariable String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime endTime) {
        try {
            boolean available = reservationService.isRoomAvailable(
                roomId, startTime, endTime
            );
            return ResponseEntity.ok(available);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<ReservationDTO> addParticipant(
            @PathVariable String id,
            @RequestBody ParticipantDTO participantDTO) {
        try {
            Participant participant = DTOMapper.toDomain(participantDTO);
            Reservation reservation = reservationService.addParticipant(id, participant);
            return ResponseEntity.ok(DTOMapper.toDTO(reservation));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/participants/{participantId}")
    public ResponseEntity<ReservationDTO> removeParticipant(
            @PathVariable String id,
            @PathVariable String participantId) {
        try {
            Reservation reservation = 
                reservationService.removeParticipant(id, participantId);
            return ResponseEntity.ok(DTOMapper.toDTO(reservation));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable String id) {
        try {
            Reservation reservation = reservationService.cancelReservation(id);
            return ResponseEntity.ok(DTOMapper.toDTO(reservation));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
