package com.reservation.application.services;

import com.reservation.domain.aggregates.Reservation;
import com.reservation.domain.entities.Participant;
import com.reservation.domain.entities.Room;
import com.reservation.domain.repositories.ReservationRepository;
import com.reservation.domain.repositories.RoomRepository;
import com.reservation.domain.valueobjects.ReservationPeriod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service applicatif pour la gestion des réservations.
 * Couche Application : implémente les cas d'usage métier.
 * 
 * Responsabilités :
 * - Orchestrer les opérations sur les aggregates
 * - Vérifier les règles métier inter-aggregates
 * - Coordonner les repositories
 */
@Service
@Transactional
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository,
                             RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Crée une nouvelle réservation.
     * 
     * Règles métier appliquées :
     * 1. La salle doit exister
     * 2. Aucune réservation active ne doit chevaucher la période demandée
     * 3. Le nombre de participants ne doit pas dépasser la capacité
     */
    public Reservation createReservation(String roomId, 
                                        LocalDateTime startTime,
                                        LocalDateTime endTime,
                                        List<Participant> participants) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Salle non trouvée avec l'ID : " + roomId
            ));
        
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        
        checkForConflictingReservations(roomId, period);
        
        Reservation reservation = Reservation.create(room, period, participants);
        
        return reservationRepository.save(reservation);
    }

    /**
     * Vérifie qu'il n'y a pas de réservations conflictuelles.
     * Règle métier essentielle : éviter les doubles réservations.
     */
    private void checkForConflictingReservations(String roomId, ReservationPeriod period) {
        List<Reservation> existingReservations = 
            reservationRepository.findActiveReservationsByRoomIdAndPeriod(roomId, period);
        
        for (Reservation existing : existingReservations) {
            if (existing.overlapsWith(period)) {
                throw new IllegalStateException(
                    "La salle est déjà réservée sur cette période. " +
                    "Réservation existante : " + existing.getId() +
                    " du " + existing.getPeriod().getStartTime() +
                    " au " + existing.getPeriod().getEndTime()
                );
            }
        }
    }

    /**
     * Récupère une réservation par son ID.
     */
    public Reservation getReservationById(String id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Réservation non trouvée avec l'ID : " + id
            ));
    }

    /**
     * Récupère toutes les réservations.
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Récupère les réservations actives pour une salle.
     */
    public List<Reservation> getActiveReservationsByRoom(String roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new IllegalArgumentException(
                "Salle non trouvée avec l'ID : " + roomId
            );
        }
        return reservationRepository.findActiveReservationsByRoomId(roomId);
    }

    /**
     * Vérifie la disponibilité d'une salle sur une période.
     */
    public boolean isRoomAvailable(String roomId, 
                                   LocalDateTime startTime, 
                                   LocalDateTime endTime) {
        if (!roomRepository.existsById(roomId)) {
            throw new IllegalArgumentException(
                "Salle non trouvée avec l'ID : " + roomId
            );
        }
        
        ReservationPeriod period = new ReservationPeriod(startTime, endTime);
        List<Reservation> existingReservations = 
            reservationRepository.findActiveReservationsByRoomIdAndPeriod(roomId, period);
        
        return existingReservations.stream()
            .noneMatch(reservation -> reservation.overlapsWith(period));
    }

    /**
     * Ajoute un participant à une réservation.
     * L'aggregate Reservation protège l'invariant de capacité.
     */
    public Reservation addParticipant(String reservationId, Participant participant) {
        Reservation reservation = getReservationById(reservationId);
        reservation.addParticipant(participant);
        return reservationRepository.save(reservation);
    }

    /**
     * Retire un participant d'une réservation.
     */
    public Reservation removeParticipant(String reservationId, String participantId) {
        Reservation reservation = getReservationById(reservationId);
        reservation.removeParticipant(participantId);
        return reservationRepository.save(reservation);
    }

    /**
     * Annule une réservation.
     * L'aggregate Reservation gère la transition d'état.
     */
    public Reservation cancelReservation(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        reservation.cancel();
        return reservationRepository.save(reservation);
    }

    /**
     * Supprime une réservation (hard delete).
     */
    public void deleteReservation(String id) {
        if (!reservationRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException(
                "Réservation non trouvée avec l'ID : " + id
            );
        }
        reservationRepository.delete(id);
    }
}
