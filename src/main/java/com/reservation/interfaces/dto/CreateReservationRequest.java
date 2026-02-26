package com.reservation.interfaces.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CreateReservationRequest {
    private String roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ParticipantDTO> participants;

    public CreateReservationRequest() {
    }

    public CreateReservationRequest(String roomId, LocalDateTime startTime, 
                                   LocalDateTime endTime, List<ParticipantDTO> participants) {
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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
}
