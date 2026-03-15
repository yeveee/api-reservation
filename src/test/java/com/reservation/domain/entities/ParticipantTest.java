package com.reservation.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {

    @Test
    void shouldCreateValidParticipant() {
        Participant participant = new Participant("Alice Dupont", "alice@example.com");

        assertNotNull(participant);
        assertNotNull(participant.getId());
        assertEquals("Alice Dupont", participant.getName());
        assertEquals("alice@example.com", participant.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant(null, "alice@example.com");
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("", "alice@example.com");
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("Alice Dupont", null);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participant("Alice Dupont", "invalid-email");
        });
    }

    @Test
    void shouldAcceptValidEmailFormats() {
        assertDoesNotThrow(() -> new Participant("Alice", "alice@example.com"));
        assertDoesNotThrow(() -> new Participant("Bob", "bob.smith@company.co.uk"));
        assertDoesNotThrow(() -> new Participant("Charlie", "charlie+test@domain.org"));
    }

    @Test
    void shouldUpdateName() {
        Participant participant = new Participant("Alice Dupont", "alice@example.com");
        
        participant.setName("Alice Martin");
        
        assertEquals("Alice Martin", participant.getName());
    }

    @Test
    void shouldUpdateEmail() {
        Participant participant = new Participant("Alice Dupont", "alice@example.com");
        
        participant.setEmail("alice.dupont@newdomain.com");
        
        assertEquals("alice.dupont@newdomain.com", participant.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToInvalidEmail() {
        Participant participant = new Participant("Alice Dupont", "alice@example.com");
        
        assertThrows(IllegalArgumentException.class, () -> {
            participant.setEmail("invalid");
        });
    }
}
