package com.reservation.domain.aggregates;

import com.reservation.domain.entities.Participant;
import com.reservation.domain.entities.Room;
import com.reservation.domain.valueobjects.ReservationPeriod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * AGGREGATE ROOT : Reservation
 * 
 * Responsabilités :
 * - Garantir la cohérence des réservations
 * - Protéger les invariants métier
 * - Gérer le cycle de vie des participants (entities internes)
 * 
 * Invariants protégés :
 * 1. Une réservation ne peut pas dépasser la capacité de la salle
 * 2. Une réservation ne peut pas être modifiée si elle est annulée
 * 3. Les participants ne peuvent être ajoutés que si la capacité le permet
 * 4. La période de réservation doit être valide
 */
public class Reservation {
    
    private final String id;
    private final Room room;
    private final ReservationPeriod period;
    private final List<Participant> participants;
    private ReservationStatus status;

    /**
     * Constructeur privé pour forcer l'utilisation de la factory method.
     */
    private Reservation(String id, Room room, ReservationPeriod period, 
                       List<Participant> participants, ReservationStatus status) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.room = room;
        this.period = period;
        this.participants = new ArrayList<>(participants);
        this.status = status;
    }

    /**
     * Factory method pour créer une nouvelle réservation.
     * Garantit que tous les invariants sont respectés dès la création.
     */
    public static Reservation create(Room room, ReservationPeriod period, 
                                    List<Participant> initialParticipants) {
        validateCreation(room, period, initialParticipants);
        
        if (initialParticipants.size() > room.getMaxCapacity()) {
            throw new IllegalStateException(
                "Le nombre de participants (" + initialParticipants.size() + 
                ") dépasse la capacité de la salle (" + room.getMaxCapacity() + ")"
            );
        }
        
        return new Reservation(null, room, period, initialParticipants, ReservationStatus.ACTIVE);
    }

    /**
     * Reconstitution d'une réservation existante (pour la persistence).
     */
    public static Reservation reconstitute(String id, Room room, ReservationPeriod period,
                                          List<Participant> participants, ReservationStatus status) {
        return new Reservation(id, room, period, participants, status);
    }

    /**
     * Valide les paramètres de création.
     */
    private static void validateCreation(Room room, ReservationPeriod period, 
                                         List<Participant> participants) {
        if (room == null) {
            throw new IllegalArgumentException("La salle ne peut pas être nulle");
        }
        if (period == null) {
            throw new IllegalArgumentException("La période ne peut pas être nulle");
        }
        if (participants == null) {
            throw new IllegalArgumentException("La liste de participants ne peut pas être nulle");
        }
    }

    /**
     * Ajoute un participant à la réservation.
     * Invariant : respecte la capacité maximale de la salle.
     */
    public void addParticipant(Participant participant) {
        ensureNotCancelled();
        
        if (participant == null) {
            throw new IllegalArgumentException("Le participant ne peut pas être nul");
        }
        
        if (isFull()) {
            throw new IllegalStateException(
                "Impossible d'ajouter un participant : la salle est pleine " +
                "(capacité : " + room.getMaxCapacity() + ")"
            );
        }
        
        if (participants.stream().anyMatch(p -> p.getId().equals(participant.getId()))) {
            throw new IllegalStateException(
                "Ce participant est déjà inscrit à cette réservation"
            );
        }
        
        participants.add(participant);
    }

    /**
     * Retire un participant de la réservation.
     */
    public void removeParticipant(String participantId) {
        ensureNotCancelled();
        
        if (participantId == null) {
            throw new IllegalArgumentException("L'ID du participant ne peut pas être nul");
        }
        
        boolean removed = participants.removeIf(p -> p.getId().equals(participantId));
        
        if (!removed) {
            throw new IllegalStateException(
                "Participant non trouvé dans cette réservation"
            );
        }
    }

    /**
     * Annule la réservation.
     * Transition d'état : ACTIVE -> CANCELLED
     */
    public void cancel() {
        if (status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Cette réservation est déjà annulée");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    /**
     * Vérifie si la réservation est pleine.
     * Règle métier : comparaison du nombre de participants avec la capacité.
     */
    public boolean isFull() {
        return participants.size() >= room.getMaxCapacity();
    }

    /**
     * Vérifie si cette réservation chevauche une autre période.
     * Essentiel pour éviter les doubles réservations.
     */
    public boolean overlapsWith(ReservationPeriod otherPeriod) {
        return this.period.overlapsWith(otherPeriod);
    }

    /**
     * Vérifie que la réservation n'est pas annulée avant modification.
     */
    private void ensureNotCancelled() {
        if (status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException(
                "Impossible de modifier une réservation annulée"
            );
        }
    }

    public String getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public ReservationPeriod getPeriod() {
        return period;
    }

    public List<Participant> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == ReservationStatus.ACTIVE;
    }

    public boolean isCancelled() {
        return status == ReservationStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", room=" + room.getName() +
                ", period=" + period +
                ", participantsCount=" + participants.size() +
                ", status=" + status +
                '}';
    }

    /**
     * Enum représentant les états possibles d'une réservation.
     */
    public enum ReservationStatus {
        ACTIVE,
        CANCELLED
    }
}
