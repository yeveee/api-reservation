package com.reservation.infrastructure.persistence.mappers;

import com.reservation.domain.entities.Room;
import com.reservation.infrastructure.persistence.entities.RoomJpaEntity;

/**
 * Mapper pour convertir entre le modèle du domaine et le modèle JPA.
 * Pattern Anti-Corruption Layer : protège le domaine de la contamination infrastructure.
 */
public class RoomMapper {
    
    /**
     * Convertit une entité du domaine vers une entité JPA.
     */
    public static RoomJpaEntity toJpaEntity(Room room) {
        if (room == null) {
            return null;
        }
        return new RoomJpaEntity(
            room.getId(),
            room.getName(),
            room.getMaxCapacity()
        );
    }

    /**
     * Convertit une entité JPA vers une entité du domaine.
     */
    public static Room toDomain(RoomJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return new Room(
            jpaEntity.getId(),
            jpaEntity.getName(),
            jpaEntity.getMaxCapacity()
        );
    }
}
