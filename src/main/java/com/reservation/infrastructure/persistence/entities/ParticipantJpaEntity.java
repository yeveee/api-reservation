package com.reservation.infrastructure.persistence.entities;

import jakarta.persistence.*;

/**
 * Entité JPA pour la persistence de Participant.
 * Entity interne à l'Aggregate Reservation.
 */
@Entity
@Table(name = "participants")
public class ParticipantJpaEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private ReservationJpaEntity reservation;

    public ParticipantJpaEntity() {
    }

    public ParticipantJpaEntity(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ReservationJpaEntity getReservation() {
        return reservation;
    }

    public void setReservation(ReservationJpaEntity reservation) {
        this.reservation = reservation;
    }
}
