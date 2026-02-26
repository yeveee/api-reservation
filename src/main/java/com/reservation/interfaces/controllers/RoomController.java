package com.reservation.interfaces.controllers;

import com.reservation.application.services.RoomService;
import com.reservation.domain.entities.Room;
import com.reservation.interfaces.dto.RoomDTO;
import com.reservation.interfaces.mappers.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des salles.
 * Couche Interfaces : point d'entrée de l'API.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        try {
            Room room = roomService.createRoom(roomDTO.getName(), roomDTO.getMaxCapacity());
            return ResponseEntity.status(HttpStatus.CREATED).body(DTOMapper.toDTO(room));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable String id) {
        try {
            Room room = roomService.getRoomById(id);
            return ResponseEntity.ok(DTOMapper.toDTO(room));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomDTO> roomDTOs = rooms.stream()
            .map(DTOMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(roomDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable String id, 
                                             @RequestBody RoomDTO roomDTO) {
        try {
            Room room = roomService.updateRoom(id, roomDTO.getName(), roomDTO.getMaxCapacity());
            return ResponseEntity.ok(DTOMapper.toDTO(room));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
