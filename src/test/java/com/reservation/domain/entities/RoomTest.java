package com.reservation.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void shouldCreateValidRoom() {
        Room room = new Room("Salle de Yoga", 20);

        assertNotNull(room);
        assertNotNull(room.getId());
        assertEquals("Salle de Yoga", room.getName());
        assertEquals(20, room.getMaxCapacity());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Room(null, 20);
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Room("", 20);
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Room("   ", 20);
        });
    }

    @Test
    void shouldThrowExceptionWhenCapacityIsZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Room("Salle de Yoga", 0);
        });
    }

    @Test
    void shouldThrowExceptionWhenCapacityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Room("Salle de Yoga", -5);
        });
    }

    @Test
    void shouldUpdateName() {
        Room room = new Room("Salle de Yoga", 20);
        
        room.setName("Salle de Pilates");
        
        assertEquals("Salle de Pilates", room.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToInvalidName() {
        Room room = new Room("Salle de Yoga", 20);
        
        assertThrows(IllegalArgumentException.class, () -> {
            room.setName("");
        });
    }

    @Test
    void shouldUpdateCapacity() {
        Room room = new Room("Salle de Yoga", 20);
        
        room.setMaxCapacity(30);
        
        assertEquals(30, room.getMaxCapacity());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToInvalidCapacity() {
        Room room = new Room("Salle de Yoga", 20);
        
        assertThrows(IllegalArgumentException.class, () -> {
            room.setMaxCapacity(0);
        });
    }

    @Test
    void shouldGenerateUniqueIds() {
        Room room1 = new Room("Salle 1", 10);
        Room room2 = new Room("Salle 2", 20);

        assertNotEquals(room1.getId(), room2.getId());
    }
}
