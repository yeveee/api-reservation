package com.reservation.domain.repositories;

import com.reservation.domain.entities.Room;

import java.util.List;
import java.util.Optional;

/**
 * Port (interface) du repository pour l'Entity Room.
 * Architecture Hexagonale : abstraction des opérations de persistence.
 */
public interface RoomRepository {
    
    /**
     * Sauvegarde une salle (création ou mise à jour).
     */
    Room save(Room room);
    
    /**
     * Recherche une salle par son identifiant.
     */
    Optional<Room> findById(String id);
    
    /**
     * Récupère toutes les salles.
     */
    List<Room> findAll();
    
    /**
     * Recherche une salle par son nom.
     */
    Optional<Room> findByName(String name);
    
    /**
     * Supprime une salle.
     */
    void delete(String id);
    
    /**
     * Vérifie si une salle existe.
     */
    boolean existsById(String id);
}
