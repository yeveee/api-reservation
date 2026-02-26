package com.reservation.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA pour la persistence de Reservation (Aggregate Root).
 * Gère la relation avec les participants (cascade).
 */
@Entity
@Table(name = "reservations")
public class ReservationJpaEntity {
    
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomJpaEntity room;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantJpaEntity> participants = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatusJpa status;

    public ReservationJpaEntity() {
    }

    public ReservationJpaEntity(String id, RoomJpaEntity room, 
                               LocalDateTime startTime, LocalDateTime endTime,
                               ReservationStatusJpa status) {
        this.id = id;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public void addParticipant(ParticipantJpaEntity participant) {
        participants.add(participant);
        participant.setReservation(this);
    }

    public void removeParticipant(ParticipantJpaEntity participant) {
        participants.remove(participant);
        participant.setReservation(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RoomJpaEntity getRoom() {
        return room;
    }

    public void setRoom(RoomJpaEntity room) {
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

    public List<ParticipantJpaEntity> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantJpaEntity> participants) {
        this.participants = participants;
    }

    public ReservationStatusJpa getStatus() {
        return status;
    }

    public void setStatus(ReservationStatusJpa status) {
        this.status = status;
    }

    public enum ReservationStatusJpa {
        ACTIVE,
        CANCELLED
    }
}
