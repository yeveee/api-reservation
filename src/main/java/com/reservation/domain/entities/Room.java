package com.reservation.domain.entities;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity représentant une salle de sport.
 * Contient les informations essentielles : nom et capacité maximale.
 */
public class Room {
    
    private final String id;
    private String name;
    private int maxCapacity;

    public Room(String id, String name, int maxCapacity) {
        validateRoom(name, maxCapacity);
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.maxCapacity = maxCapacity;
    }

    public Room(String name, int maxCapacity) {
        this(null, name, maxCapacity);
    }

    /**
     * Valide les invariants de la salle.
     */
    private void validateRoom(String name, int maxCapacity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide");
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("La capacité maximale doit être supérieure à 0");
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
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide");
        }
        this.name = name;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("La capacité maximale doit être supérieure à 0");
        }
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}
