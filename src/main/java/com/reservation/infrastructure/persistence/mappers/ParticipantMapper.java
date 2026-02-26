package com.reservation.infrastructure.persistence.mappers;

import com.reservation.domain.entities.Participant;
import com.reservation.infrastructure.persistence.entities.ParticipantJpaEntity;

/**
 * Mapper pour convertir entre Participant du domaine et ParticipantJpaEntity.
 */
public class ParticipantMapper {
    
    public static ParticipantJpaEntity toJpaEntity(Participant participant) {
        if (participant == null) {
            return null;
        }
        return new ParticipantJpaEntity(
            participant.getId(),
            participant.getName(),
            participant.getEmail()
        );
    }

    public static Participant toDomain(ParticipantJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return new Participant(
            jpaEntity.getId(),
            jpaEntity.getName(),
            jpaEntity.getEmail()
        );
    }
}
