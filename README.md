# API de Réservation de Salles de Sport

## Description du Projet

Cette application est une API REST développée avec **Java Spring Boot** permettant la gestion de réservations de salles de sport. Le projet implémente une architecture hexagonale (Ports & Adapters) combinée aux principes du Domain-Driven Design (DDD).

## Technologies Utilisées

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (base de données en mémoire)
- **Maven** (gestion des dépendances)

## Architecture du Projet

```
src/main/java/com/reservation/
├── domain/                          # Couche Domaine (cœur métier)
│   ├── aggregates/
│   │   └── Reservation.java        # Aggregate Root
│   ├── entities/
│   │   ├── Room.java               # Entity
│   │   └── Participant.java        # Entity
│   ├── valueobjects/
│   │   └── ReservationPeriod.java  # Value Object
│   └── repositories/               # Ports (interfaces)
│       ├── ReservationRepository.java
│       └── RoomRepository.java
│
├── application/                     # Couche Application (cas d'usage)
│   └── services/
│       ├── ReservationService.java
│       └── RoomService.java
│
├── infrastructure/                  # Couche Infrastructure (adaptateurs)
│   └── persistence/
│       ├── entities/               # Entités JPA
│       │   ├── ReservationJpaEntity.java
│       │   ├── RoomJpaEntity.java
│       │   └── ParticipantJpaEntity.java
│       ├── mappers/                # Mappers Domain ↔ JPA
│       │   ├── ReservationMapper.java
│       │   ├── RoomMapper.java
│       │   └── ParticipantMapper.java
│       ├── jpa/                    # Repositories Spring Data
│       │   ├── SpringDataReservationRepository.java
│       │   └── SpringDataRoomRepository.java
│       └── adapters/               # Implémentations des ports
│           ├── ReservationRepositoryImpl.java
│           └── RoomRepositoryImpl.java
│
└── interfaces/                      # Couche Interfaces (API REST)
    ├── controllers/
    │   ├── ReservationController.java
    │   └── RoomController.java
    ├── dto/
    │   ├── ReservationDTO.java
    │   ├── RoomDTO.java
    │   ├── ParticipantDTO.java
    │   └── CreateReservationRequest.java
    └── mappers/
        └── DTOMapper.java
```

## Fonctionnalités Implémentées

### Gestion des Salles
- ✅ Créer une salle
- ✅ Consulter une salle
- ✅ Lister toutes les salles
- ✅ Modifier une salle
- ✅ Supprimer une salle

### Gestion des Réservations
- ✅ Créer une réservation
- ✅ Consulter une réservation
- ✅ Lister toutes les réservations
- ✅ Vérifier la disponibilité d'une salle
- ✅ Ajouter un participant à une réservation
- ✅ Retirer un participant d'une réservation
- ✅ Annuler une réservation
- ✅ Supprimer une réservation

## Règles Métier Implémentées

1. **Éviter les doubles réservations** : Une salle ne peut pas être réservée sur des périodes qui se chevauchent
2. **Respect de la capacité maximale** : Le nombre de participants ne peut pas dépasser la capacité de la salle
3. **Validation des périodes** : La date de début doit être antérieure à la date de fin
4. **Protection des réservations annulées** : Une réservation annulée ne peut plus être modifiée
5. **Validation des données** : Tous les champs obligatoires sont validés

## Démarrage de l'Application

### Prérequis
- Java 17 ou supérieur
- Maven 3.6 ou supérieur

### Installation et Exécution

```bash
# Cloner le projet
cd api-reservation

# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

L'application démarre sur le port **8080**.

### Accès à la Console H2

URL : http://localhost:8080/h2-console
- JDBC URL : `jdbc:h2:mem:reservationdb`
- Username : `sa`
- Password : (vide)

## Endpoints de l'API

### Salles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/rooms` | Créer une salle |
| GET | `/api/rooms/{id}` | Récupérer une salle |
| GET | `/api/rooms` | Lister toutes les salles |
| PUT | `/api/rooms/{id}` | Modifier une salle |
| DELETE | `/api/rooms/{id}` | Supprimer une salle |

### Réservations

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/reservations` | Créer une réservation |
| GET | `/api/reservations/{id}` | Récupérer une réservation |
| GET | `/api/reservations` | Lister toutes les réservations |
| GET | `/api/reservations/room/{roomId}` | Réservations actives d'une salle |
| GET | `/api/reservations/room/{roomId}/availability` | Vérifier disponibilité |
| POST | `/api/reservations/{id}/participants` | Ajouter un participant |
| DELETE | `/api/reservations/{id}/participants/{participantId}` | Retirer un participant |
| PUT | `/api/reservations/{id}/cancel` | Annuler une réservation |
| DELETE | `/api/reservations/{id}` | Supprimer une réservation |

## Exemples d'Utilisation

### Créer une Salle

```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Salle de Yoga",
    "maxCapacity": 20
  }'
```

### Créer une Réservation

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "room-id-here",
    "startTime": "2026-03-01T10:00:00",
    "endTime": "2026-03-01T11:00:00",
    "participants": [
      {
        "name": "Jean Dupont",
        "email": "jean.dupont@example.com"
      }
    ]
  }'
```

### Vérifier la Disponibilité

```bash
curl "http://localhost:8080/api/reservations/room/{roomId}/availability?startTime=2026-03-01T10:00:00&endTime=2026-03-01T11:00:00"
```

## Documentation Académique

Pour une explication détaillée de l'architecture, des choix de conception et des principes DDD appliqués, consultez le document **DOCUMENTATION_ACADEMIQUE.md**.

## Auteur

Projet académique - API de Réservation de Salles de Sport
