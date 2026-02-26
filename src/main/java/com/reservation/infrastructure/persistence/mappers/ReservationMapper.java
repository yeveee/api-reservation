package com.reservation.infrastructure.persistence.mappers;

import com.reservation.domain.aggregates.Reservation;
import com.reservation.domain.entities.Participant;
import com.reservation.domain.entities.Room;
import com.reservation.domain.valueobjects.ReservationPeriod;
import com.reservation.infrastructure.persistence.entities.ParticipantJpaEntity;
import com.reservation.infrastructure.persistence.entities.ReservationJpaEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre Reservation (Aggregate) et ReservationJpaEntity.
 * Gère la conversion complexe incluant les entities internes.
 */
public class ReservationMapper {
    
    /**
     * Convertit un Aggregate Reservation vers une entité JPA.
     */
    public static ReservationJpaEntity toJpaEntity(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        
        ReservationJpaEntity jpaEntity = new ReservationJpaEntity(
            reservation.getId(),
            RoomMapper.toJpaEntity(reservation.getRoom()),
            reservation.getPeriod().getStartTime(),
            reservation.getPeriod().getEndTime(),
            ReservationJpaEntity.ReservationStatusJpa.valueOf(reservation.getStatus().name())
        );
        
        List<ParticipantJpaEntity> participantEntities = reservation.getParticipants()
            .stream()
            .map(ParticipantMapper::toJpaEntity)
            .collect(Collectors.toList());
        
        participantEntities.forEach(jpaEntity::addParticipant);
        
        return jpaEntity;
    }

    /**
     * Convertit une entité JPA vers un Aggregate Reservation.
     * Utilise la méthode reconstitute pour recréer l'aggregate.
     */
    public static Reservation toDomain(ReservationJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        Room room = RoomMapper.toDomain(jpaEntity.getRoom());
        
        ReservationPeriod period = new ReservationPeriod(
            jpaEntity.getStartTime(),
            jpaEntity.getEndTime()
        );
        
        List<Participant> participants = jpaEntity.getParticipants()
            .stream()
            .map(ParticipantMapper::toDomain)
            .collect(Collectors.toList());
        
        Reservation.ReservationStatus status = 
            Reservation.ReservationStatus.valueOf(jpaEntity.getStatus().name());
        
        return Reservation.reconstitute(
            jpaEntity.getId(),
            room,
            period,
            participants,
            status
        );
    }
}
