package com.reservation.domain.entities;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity représentant un participant à une réservation.
 * Entity interne à l'Aggregate Reservation.
 */
public class Participant {
    
    private final String id;
    private String name;
    private String email;

    public Participant(String id, String name, String email) {
        validateParticipant(name, email);
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    public Participant(String name, String email) {
        this(null, name, email);
    }

    /**
     * Valide les invariants du participant.
     */
    private void validateParticipant(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du participant ne peut pas être vide");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'email du participant est invalide");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du participant ne peut pas être vide");
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'email du participant est invalide");
        }
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
