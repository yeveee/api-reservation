package com.reservation.infrastructure.persistence.adapters;

import com.reservation.domain.entities.Room;
import com.reservation.domain.repositories.RoomRepository;
import com.reservation.infrastructure.persistence.entities.RoomJpaEntity;
import com.reservation.infrastructure.persistence.jpa.SpringDataRoomRepository;
import com.reservation.infrastructure.persistence.mappers.RoomMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du port RoomRepository.
 * Adaptateur Infrastructure : implémente l'interface du domaine.
 * Architecture Hexagonale : le domaine ne dépend pas de Spring Data JPA.
 */
@Component
public class RoomRepositoryImpl implements RoomRepository {
    
    private final SpringDataRoomRepository springDataRepository;

    public RoomRepositoryImpl(SpringDataRoomRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Room save(Room room) {
        RoomJpaEntity jpaEntity = RoomMapper.toJpaEntity(room);
        RoomJpaEntity saved = springDataRepository.save(jpaEntity);
        return RoomMapper.toDomain(saved);
    }

    @Override
    public Optional<Room> findById(String id) {
        return springDataRepository.findById(id)
            .map(RoomMapper::toDomain);
    }

    @Override
    public List<Room> findAll() {
        return springDataRepository.findAll()
            .stream()
            .map(RoomMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Room> findByName(String name) {
        return springDataRepository.findByName(name)
            .map(RoomMapper::toDomain);
    }

    @Override
    public void delete(String id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return springDataRepository.existsById(id);
    }
}
