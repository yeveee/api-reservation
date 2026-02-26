package com.reservation.infrastructure.persistence.jpa;

import com.reservation.infrastructure.persistence.entities.RoomJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository pour RoomJpaEntity.
 */
@Repository
public interface SpringDataRoomRepository extends JpaRepository<RoomJpaEntity, String> {
    
    Optional<RoomJpaEntity> findByName(String name);
}
