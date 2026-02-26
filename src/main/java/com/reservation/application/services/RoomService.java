package com.reservation.application.services;

import com.reservation.domain.entities.Room;
import com.reservation.domain.repositories.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service applicatif pour la gestion des salles.
 * Couche Application : orchestre les opérations métier.
 */
@Service
@Transactional
public class RoomService {
    
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Crée une nouvelle salle.
     */
    public Room createRoom(String name, int maxCapacity) {
        if (roomRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException(
                "Une salle avec le nom '" + name + "' existe déjà"
            );
        }
        
        Room room = new Room(name, maxCapacity);
        return roomRepository.save(room);
    }

    /**
     * Récupère une salle par son ID.
     */
    public Room getRoomById(String id) {
        return roomRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Salle non trouvée avec l'ID : " + id
            ));
    }

    /**
     * Récupère toutes les salles.
     */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Met à jour une salle existante.
     */
    public Room updateRoom(String id, String name, int maxCapacity) {
        Room room = getRoomById(id);
        
        if (name != null && !name.equals(room.getName())) {
            roomRepository.findByName(name).ifPresent(existingRoom -> {
                if (!existingRoom.getId().equals(id)) {
                    throw new IllegalArgumentException(
                        "Une autre salle avec le nom '" + name + "' existe déjà"
                    );
                }
            });
            room.setName(name);
        }
        
        if (maxCapacity > 0) {
            room.setMaxCapacity(maxCapacity);
        }
        
        return roomRepository.save(room);
    }

    /**
     * Supprime une salle.
     */
    public void deleteRoom(String id) {
        if (!roomRepository.existsById(id)) {
            throw new IllegalArgumentException(
                "Salle non trouvée avec l'ID : " + id
            );
        }
        roomRepository.delete(id);
    }
}
