package com.reservation.infrastructure.persistence.entities;

import jakarta.persistence.*;

/**
 * Entité JPA pour la persistence de Room.
 * Couche Infrastructure : adaptateur pour la base de données.
 * Séparation claire entre le modèle du domaine et le modèle de persistence.
 */
@Entity
@Table(name = "rooms")
public class RoomJpaEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private int maxCapacity;

    public RoomJpaEntity() {
    }

    public RoomJpaEntity(String id, String name, int maxCapacity) {
        this.id = id;
        this.name = name;
        this.maxCapacity = maxCapacity;
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

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
