package com.reservation.infrastructure.persistence.adapters;

import com.reservation.domain.aggregates.Reservation;
import com.reservation.domain.repositories.ReservationRepository;
import com.reservation.domain.valueobjects.ReservationPeriod;
import com.reservation.infrastructure.persistence.entities.ReservationJpaEntity;
import com.reservation.infrastructure.persistence.jpa.SpringDataReservationRepository;
import com.reservation.infrastructure.persistence.mappers.ReservationMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du port ReservationRepository.
 * Adaptateur Infrastructure : traduit les appels du domaine vers Spring Data JPA.
 */
@Component
public class ReservationRepositoryImpl implements ReservationRepository {
    
    private final SpringDataReservationRepository springDataRepository;

    public ReservationRepositoryImpl(SpringDataReservationRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity jpaEntity = ReservationMapper.toJpaEntity(reservation);
        ReservationJpaEntity saved = springDataRepository.save(jpaEntity);
        return ReservationMapper.toDomain(saved);
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return springDataRepository.findById(id)
            .map(ReservationMapper::toDomain);
    }

    @Override
    public List<Reservation> findAll() {
        return springDataRepository.findAll()
            .stream()
            .map(ReservationMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findActiveReservationsByRoomId(String roomId) {
        return springDataRepository.findActiveReservationsByRoomId(roomId)
            .stream()
            .map(ReservationMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findActiveReservationsByRoomIdAndPeriod(
            String roomId, 
            ReservationPeriod period) {
        return springDataRepository.findActiveReservationsByRoomIdAndPeriod(
                roomId,
                period.getStartTime(),
                period.getEndTime()
            )
            .stream()
            .map(ReservationMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        springDataRepository.deleteById(id);
    }
}
