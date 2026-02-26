package com.reservation.interfaces.mappers;

import com.reservation.domain.aggregates.Reservation;
import com.reservation.domain.entities.Participant;
import com.reservation.domain.entities.Room;
import com.reservation.interfaces.dto.ParticipantDTO;
import com.reservation.interfaces.dto.ReservationDTO;
import com.reservation.interfaces.dto.RoomDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre les objets du domaine et les DTOs.
 * Couche Interfaces : protège le domaine de l'exposition directe.
 */
public class DTOMapper {
    
    public static RoomDTO toDTO(Room room) {
        if (room == null) {
            return null;
        }
        return new RoomDTO(
            room.getId(),
            room.getName(),
            room.getMaxCapacity()
        );
    }

    public static ParticipantDTO toDTO(Participant participant) {
        if (participant == null) {
            return null;
        }
        return new ParticipantDTO(
            participant.getId(),
            participant.getName(),
            participant.getEmail()
        );
    }

    public static Participant toDomain(ParticipantDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Participant(
            dto.getId(),
            dto.getName(),
            dto.getEmail()
        );
    }

    public static ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        
        RoomDTO roomDTO = toDTO(reservation.getRoom());
        
        List<ParticipantDTO> participantDTOs = reservation.getParticipants()
            .stream()
            .map(DTOMapper::toDTO)
            .collect(Collectors.toList());
        
        return new ReservationDTO(
            reservation.getId(),
            roomDTO,
            reservation.getPeriod().getStartTime(),
            reservation.getPeriod().getEndTime(),
            participantDTOs,
            reservation.getStatus().name()
        );
    }
}
