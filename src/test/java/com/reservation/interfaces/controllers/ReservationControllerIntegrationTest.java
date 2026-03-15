package com.reservation.interfaces.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservation.interfaces.dto.CreateReservationRequest;
import com.reservation.interfaces.dto.ParticipantDTO;
import com.reservation.interfaces.dto.ReservationDTO;
import com.reservation.interfaces.dto.RoomDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String roomId;

    @BeforeEach
    void setUp() throws Exception {
        // Créer une nouvelle salle pour chaque test avec un nom unique
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setName("Salle de Test " + System.currentTimeMillis());
        roomDTO.setMaxCapacity(20);

        MvcResult result = mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RoomDTO createdRoom = objectMapper.readValue(responseBody, RoomDTO.class);
        roomId = createdRoom.getId();
    }

    @Test
    void shouldCreateReservation() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId(roomId);
        request.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        request.setEndTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0));

        ParticipantDTO participant = new ParticipantDTO();
        participant.setName("Alice");
        participant.setEmail("alice@test.com");
        request.setParticipants(Arrays.asList(participant));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.room.id").value(roomId))
                .andExpect(jsonPath("$.participants.length()").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldGetAllReservations() throws Exception {
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetReservationById() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId(roomId);
        request.setStartTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0));
        request.setEndTime(LocalDateTime.now().plusDays(1).withHour(16).withMinute(0).withSecond(0).withNano(0));

        ParticipantDTO participant = new ParticipantDTO();
        participant.setName("Bob");
        participant.setEmail("bob@test.com");
        request.setParticipants(Arrays.asList(participant));

        MvcResult createResult = mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        ReservationDTO createdReservation = objectMapper.readValue(responseBody, ReservationDTO.class);

        mockMvc.perform(get("/api/reservations/" + createdReservation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdReservation.getId()));
    }

    @Test
    void shouldCancelReservation() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId(roomId);
        request.setStartTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0));
        request.setEndTime(LocalDateTime.now().plusDays(2).withHour(12).withMinute(0).withSecond(0).withNano(0));

        ParticipantDTO participant = new ParticipantDTO();
        participant.setName("Charlie");
        participant.setEmail("charlie@test.com");
        request.setParticipants(Arrays.asList(participant));

        MvcResult createResult = mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        ReservationDTO createdReservation = objectMapper.readValue(responseBody, ReservationDTO.class);

        mockMvc.perform(put("/api/reservations/" + createdReservation.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldCheckRoomAvailability() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(3).withHour(12).withMinute(0).withSecond(0).withNano(0);

        mockMvc.perform(get("/api/reservations/room/" + roomId + "/availability")
                .param("startTime", start.toString())
                .param("endTime", end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldDetectRoomUnavailability() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(4).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(4).withHour(12).withMinute(0).withSecond(0).withNano(0);

        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId(roomId);
        request.setStartTime(start);
        request.setEndTime(end);

        ParticipantDTO participant = new ParticipantDTO();
        participant.setName("David");
        participant.setEmail("david@test.com");
        request.setParticipants(Arrays.asList(participant));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/reservations/room/" + roomId + "/availability")
                .param("startTime", start.toString())
                .param("endTime", end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void shouldReturn400WhenCreatingOverlappingReservation() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(5).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(5).withHour(12).withMinute(0).withSecond(0).withNano(0);

        CreateReservationRequest request1 = new CreateReservationRequest();
        request1.setRoomId(roomId);
        request1.setStartTime(start);
        request1.setEndTime(end);

        ParticipantDTO participant1 = new ParticipantDTO();
        participant1.setName("Eve");
        participant1.setEmail("eve@test.com");
        request1.setParticipants(Arrays.asList(participant1));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        CreateReservationRequest request2 = new CreateReservationRequest();
        request2.setRoomId(roomId);
        request2.setStartTime(start.plusMinutes(30));
        request2.setEndTime(end.plusMinutes(30));

        ParticipantDTO participant2 = new ParticipantDTO();
        participant2.setName("Frank");
        participant2.setEmail("frank@test.com");
        request2.setParticipants(Arrays.asList(participant2));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }
}
