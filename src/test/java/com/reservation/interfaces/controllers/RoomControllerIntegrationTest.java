package com.reservation.interfaces.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservation.interfaces.dto.RoomDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRoom() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName("Salle de Yoga");
        roomDTO.setMaxCapacity(20);

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Salle de Yoga"))
                .andExpect(jsonPath("$.maxCapacity").value(20));
    }

    @Test
    void shouldGetAllRooms() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetRoomById() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName("Salle Test");
        roomDTO.setMaxCapacity(15);

        MvcResult createResult = mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        RoomDTO createdRoom = objectMapper.readValue(responseBody, RoomDTO.class);

        mockMvc.perform(get("/api/rooms/" + createdRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdRoom.getId()))
                .andExpect(jsonPath("$.name").value("Salle Test"))
                .andExpect(jsonPath("$.maxCapacity").value(15));
    }

    @Test
    void shouldUpdateRoom() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName("Salle Originale");
        roomDTO.setMaxCapacity(10);

        MvcResult createResult = mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        RoomDTO createdRoom = objectMapper.readValue(responseBody, RoomDTO.class);

        RoomDTO updateDTO = new RoomDTO();
        updateDTO.setName("Salle Modifiée");
        updateDTO.setMaxCapacity(25);

        mockMvc.perform(put("/api/rooms/" + createdRoom.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salle Modifiée"))
                .andExpect(jsonPath("$.maxCapacity").value(25));
    }

    @Test
    void shouldDeleteRoom() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName("Salle à Supprimer");
        roomDTO.setMaxCapacity(10);

        MvcResult createResult = mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        RoomDTO createdRoom = objectMapper.readValue(responseBody, RoomDTO.class);

        mockMvc.perform(delete("/api/rooms/" + createdRoom.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/rooms/" + createdRoom.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenRoomNotFound() throws Exception {
        mockMvc.perform(get("/api/rooms/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenCreatingInvalidRoom() throws Exception {
        RoomDTO invalidRoom = new RoomDTO();
        invalidRoom.setName("");
        invalidRoom.setMaxCapacity(0);

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRoom)))
                .andExpect(status().isBadRequest());
    }
}
