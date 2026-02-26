package com.reservation.interfaces.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationDTO {
    private String id;
    private RoomDTO room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ParticipantDTO> participants;
    private String status;

    public ReservationDTO() {
    }

    public ReservationDTO(String id, RoomDTO room, LocalDateTime startTime, 
                         LocalDateTime endTime, List<ParticipantDTO> participants, 
                         String status) {
        this.id = id;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void setRoom(RoomDTO room) {
        this.room = room;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
