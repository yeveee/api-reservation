package com.reservation.application.services;

import com.reservation.domain.entities.Room;
import com.reservation.domain.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room("room-id", "Salle de Yoga", 20);
    }

    @Test
    void shouldCreateRoom() {
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room created = roomService.createRoom("Salle de Yoga", 20);

        assertNotNull(created);
        assertEquals("Salle de Yoga", created.getName());
        assertEquals(20, created.getMaxCapacity());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void shouldGetRoomById() {
        when(roomRepository.findById("room-id")).thenReturn(Optional.of(room));

        Room found = roomService.getRoomById("room-id");

        assertNotNull(found);
        assertEquals("room-id", found.getId());
        verify(roomRepository, times(1)).findById("room-id");
    }

    @Test
    void shouldThrowExceptionWhenRoomNotFound() {
        when(roomRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            roomService.getRoomById("non-existent");
        });
    }

    @Test
    void shouldGetAllRooms() {
        Room room2 = new Room("room-2", "Salle de Pilates", 15);
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room, room2));

        List<Room> rooms = roomService.getAllRooms();

        assertEquals(2, rooms.size());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdateRoom() {
        when(roomRepository.findById("room-id")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room updated = roomService.updateRoom("room-id", "Salle de Pilates", 25);

        assertNotNull(updated);
        assertEquals("Salle de Pilates", updated.getName());
        assertEquals(25, updated.getMaxCapacity());
        verify(roomRepository, times(1)).findById("room-id");
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void shouldDeleteRoom() {
        when(roomRepository.existsById("room-id")).thenReturn(true);
        doNothing().when(roomRepository).delete("room-id");

        roomService.deleteRoom("room-id");

        verify(roomRepository, times(1)).existsById("room-id");
        verify(roomRepository, times(1)).delete("room-id");
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentRoom() {
        when(roomRepository.existsById("non-existent")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            roomService.deleteRoom("non-existent");
        });
    }
}
