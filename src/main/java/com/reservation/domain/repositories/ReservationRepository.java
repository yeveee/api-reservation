package com.reservation.domain.repositories;

import com.reservation.domain.aggregates.Reservation;
import com.reservation.domain.valueobjects.ReservationPeriod;

import java.util.List;
import java.util.Optional;

/**
 * Port (interface) du repository pour l'Aggregate Reservation.
 * Définit le contrat sans dépendre de l'infrastructure.
 * Architecture Hexagonale : le domaine définit ses besoins, l'infrastructure les implémente.
 */
public interface ReservationRepository {
    
    /**
     * Sauvegarde une réservation (création ou mise à jour).
     */
    Reservation save(Reservation reservation);
    
    /**
     * Recherche une réservation par son identifiant.
     */
    Optional<Reservation> findById(String id);
    
    /**
     * Récupère toutes les réservations.
     */
    List<Reservation> findAll();
    
    /**
     * Recherche les réservations actives pour une salle donnée.
     * Essentiel pour vérifier les conflits de réservation.
     */
    List<Reservation> findActiveReservationsByRoomId(String roomId);
    
    /**
     * Recherche les réservations actives pour une salle sur une période donnée.
     * Utilisé pour détecter les chevauchements.
     */
    List<Reservation> findActiveReservationsByRoomIdAndPeriod(
        String roomId, 
        ReservationPeriod period
    );
    
    /**
     * Supprime une réservation.
     */
    void delete(String id);
}
