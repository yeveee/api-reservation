package com.reservation.infrastructure.persistence.jpa;

import com.reservation.infrastructure.persistence.entities.ReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA Repository pour ReservationJpaEntity.
 */
@Repository
public interface SpringDataReservationRepository extends JpaRepository<ReservationJpaEntity, String> {
    
    @Query("SELECT r FROM ReservationJpaEntity r WHERE r.room.id = :roomId AND r.status = 'ACTIVE'")
    List<ReservationJpaEntity> findActiveReservationsByRoomId(@Param("roomId") String roomId);
    
    @Query("SELECT r FROM ReservationJpaEntity r WHERE r.room.id = :roomId " +
           "AND r.status = 'ACTIVE' " +
           "AND r.startTime < :endTime " +
           "AND r.endTime > :startTime")
    List<ReservationJpaEntity> findActiveReservationsByRoomIdAndPeriod(
        @Param("roomId") String roomId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
