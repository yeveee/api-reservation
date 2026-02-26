# Structure du Projet - API Réservation Salles de Sport

## Arborescence Complète

```
api-reservation/
│
├── pom.xml                                    # Configuration Maven
├── README.md                                  # Documentation utilisateur
├── DOCUMENTATION_ACADEMIQUE.md                # Documentation académique complète
├── STRUCTURE_PROJET.md                        # Ce fichier
│
└── src/
    └── main/
        ├── java/com/reservation/
        │   │
        │   ├── Application.java               # Point d'entrée Spring Boot
        │   │
        │   ├── domain/                        # ═══ COUCHE DOMAINE ═══
        │   │   │
        │   │   ├── aggregates/
        │   │   │   └── Reservation.java       # Aggregate Root
        │   │   │                              # - create()
        │   │   │                              # - addParticipant()
        │   │   │                              # - removeParticipant()
        │   │   │                              # - cancel()
        │   │   │                              # - isFull()
        │   │   │                              # - overlapsWith()
        │   │   │
        │   │   ├── entities/
        │   │   │   ├── Room.java              # Entity - Salle
        │   │   │   └── Participant.java       # Entity - Participant
        │   │   │
        │   │   ├── valueobjects/
        │   │   │   └── ReservationPeriod.java # Value Object - Période
        │   │   │                              # - overlapsWith()
        │   │   │                              # - Immuable
        │   │   │
        │   │   └── repositories/              # Ports (Interfaces)
        │   │       ├── ReservationRepository.java
        │   │       └── RoomRepository.java
        │   │
        │   ├── application/                   # ═══ COUCHE APPLICATION ═══
        │   │   └── services/
        │   │       ├── ReservationService.java # Orchestration réservations
        │   │       │                          # - createReservation()
        │   │       │                          # - addParticipant()
        │   │       │                          # - cancelReservation()
        │   │       │                          # - isRoomAvailable()
        │   │       │
        │   │       └── RoomService.java       # Orchestration salles
        │   │                                  # - createRoom()
        │   │                                  # - updateRoom()
        │   │                                  # - deleteRoom()
        │   │
        │   ├── infrastructure/                # ═══ COUCHE INFRASTRUCTURE ═══
        │   │   └── persistence/
        │   │       │
        │   │       ├── entities/              # Entités JPA
        │   │       │   ├── ReservationJpaEntity.java
        │   │       │   ├── RoomJpaEntity.java
        │   │       │   └── ParticipantJpaEntity.java
        │   │       │
        │   │       ├── mappers/               # Anti-Corruption Layer
        │   │       │   ├── ReservationMapper.java  # Domain ↔ JPA
        │   │       │   ├── RoomMapper.java
        │   │       │   └── ParticipantMapper.java
        │   │       │
        │   │       ├── jpa/                   # Spring Data Repositories
        │   │       │   ├── SpringDataReservationRepository.java
        │   │       │   └── SpringDataRoomRepository.java
        │   │       │
        │   │       └── adapters/              # Implémentations des Ports
        │   │           ├── ReservationRepositoryImpl.java
        │   │           └── RoomRepositoryImpl.java
        │   │
        │   └── interfaces/                    # ═══ COUCHE INTERFACES ═══
        │       │
        │       ├── controllers/               # Contrôleurs REST
        │       │   ├── ReservationController.java
        │       │   │   # POST   /api/reservations
        │       │   │   # GET    /api/reservations/{id}
        │       │   │   # GET    /api/reservations
        │       │   │   # POST   /api/reservations/{id}/participants
        │       │   │   # DELETE /api/reservations/{id}/participants/{participantId}
        │       │   │   # PUT    /api/reservations/{id}/cancel
        │       │   │   # GET    /api/reservations/room/{roomId}/availability
        │       │   │
        │       │   └── RoomController.java
        │       │       # POST   /api/rooms
        │       │       # GET    /api/rooms/{id}
        │       │       # GET    /api/rooms
        │       │       # PUT    /api/rooms/{id}
        │       │       # DELETE /api/rooms/{id}
        │       │
        │       ├── dto/                       # Data Transfer Objects
        │       │   ├── ReservationDTO.java
        │       │   ├── RoomDTO.java
        │       │   ├── ParticipantDTO.java
        │       │   └── CreateReservationRequest.java
        │       │
        │       └── mappers/
        │           └── DTOMapper.java         # Domain ↔ DTO
        │
        └── resources/
            └── application.properties         # Configuration Spring Boot
```

## Description des Couches

### 1. Domain Layer (Couche Domaine)

**Responsabilité** : Contenir toute la logique métier

**Caractéristiques** :
- ✅ Aucune dépendance externe
- ✅ Modèle riche avec comportement
- ✅ Protection des invariants métier
- ✅ Définit ses propres interfaces (ports)

**Composants** :
- **Aggregates** : Clusters d'objets traités comme une unité
- **Entities** : Objets avec identité
- **Value Objects** : Objets immuables sans identité
- **Repository Interfaces** : Contrats de persistence

### 2. Application Layer (Couche Application)

**Responsabilité** : Orchestrer les cas d'usage

**Caractéristiques** :
- ✅ Coordonne les opérations du domaine
- ✅ Gère les transactions
- ✅ Pas de logique métier
- ✅ Appelle les repositories

**Composants** :
- **Services** : Implémentent les cas d'usage métier

### 3. Infrastructure Layer (Couche Infrastructure)

**Responsabilité** : Implémenter les détails techniques

**Caractéristiques** :
- ✅ Implémente les ports du domaine
- ✅ Gère la persistence (JPA)
- ✅ Mappers pour isoler le domaine
- ✅ Dépend du domaine (inversion)

**Composants** :
- **JPA Entities** : Modèle de base de données
- **Mappers** : Conversion Domain ↔ JPA
- **Repository Implementations** : Adaptateurs

### 4. Interfaces Layer (Couche Interfaces)

**Responsabilité** : Exposer l'API REST

**Caractéristiques** :
- ✅ Point d'entrée HTTP
- ✅ Validation des requêtes
- ✅ Conversion DTO ↔ Domain
- ✅ Gestion des réponses HTTP

**Composants** :
- **Controllers** : Endpoints REST
- **DTOs** : Objets de transfert
- **DTO Mappers** : Conversion

## Flux de Données

### Création d'une Réservation

```
1. Client HTTP
   ↓ POST /api/reservations
2. ReservationController
   ↓ Convertit DTO → Domain
3. ReservationService
   ↓ Vérifie disponibilité
   ↓ Appelle Reservation.create()
4. Reservation (Aggregate)
   ↓ Valide invariants
   ↓ Crée l'instance
5. ReservationService
   ↓ Sauvegarde via repository
6. ReservationRepositoryImpl
   ↓ Convertit Domain → JPA
7. SpringDataReservationRepository
   ↓ Persiste en base
8. Retour inverse
   ↓ JPA → Domain → DTO
9. Client reçoit ReservationDTO
```

## Dépendances entre Couches

```
┌─────────────┐
│ Interfaces  │────┐
└─────────────┘    │
                   ▼
┌─────────────┐    ┌─────────────┐
│ Application │───►│   Domain    │◄───┐
└─────────────┘    └─────────────┘    │
                                      │
                   ┌─────────────┐    │
                   │Infrastructure│────┘
                   └─────────────┘
```

**Règle d'or** : Toutes les dépendances pointent vers le Domain

## Invariants Métier par Composant

### Reservation (Aggregate Root)

| Invariant | Méthode Protectrice |
|-----------|---------------------|
| Capacité maximale | `isFull()`, `addParticipant()` |
| Réservation annulée | `ensureNotCancelled()` |
| Participants uniques | `addParticipant()` |
| Cohérence transactionnelle | Toutes les méthodes publiques |

### ReservationPeriod (Value Object)

| Invariant | Méthode Protectrice |
|-----------|---------------------|
| start < end | `validatePeriod()` |
| Dates non nulles | `validatePeriod()` |
| Pas dans le passé | `validatePeriod()` |
| Détection chevauchement | `overlapsWith()` |

### Room (Entity)

| Invariant | Méthode Protectrice |
|-----------|---------------------|
| Nom non vide | `validateRoom()`, setters |
| Capacité > 0 | `validateRoom()`, setters |

## Patterns Utilisés

### 1. Aggregate Pattern (DDD)
- **Où** : `Reservation` est l'Aggregate Root
- **Pourquoi** : Garantir la cohérence transactionnelle

### 2. Repository Pattern (DDD)
- **Où** : `ReservationRepository`, `RoomRepository`
- **Pourquoi** : Abstraire la persistence

### 3. Ports & Adapters (Hexagonal)
- **Où** : Interfaces dans domain, implémentations dans infrastructure
- **Pourquoi** : Inverser les dépendances

### 4. Factory Method (DDD)
- **Où** : `Reservation.create()`
- **Pourquoi** : Centraliser la validation à la création

### 5. Value Object (DDD)
- **Où** : `ReservationPeriod`
- **Pourquoi** : Immuabilité et validation

### 6. Anti-Corruption Layer
- **Où** : Mappers (Domain ↔ JPA, Domain ↔ DTO)
- **Pourquoi** : Protéger le domaine de la contamination

### 7. DTO Pattern
- **Où** : Package `interfaces.dto`
- **Pourquoi** : Séparer API et modèle domaine

## Points Clés de l'Architecture

### ✅ Séparation des Préoccupations
Chaque couche a une responsabilité unique et bien définie.

### ✅ Inversion de Dépendances
L'infrastructure dépend du domaine, pas l'inverse.

### ✅ Testabilité
Le domaine peut être testé sans infrastructure.

### ✅ Maintenabilité
Changements localisés dans une seule couche.

### ✅ Évolutivité
Facile d'ajouter de nouvelles fonctionnalités.

## Commandes Utiles

```bash
# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run

# Lancer les tests
mvn test

# Générer le JAR
mvn package
```

## Endpoints API Complets

### Salles

```
POST   /api/rooms                    # Créer une salle
GET    /api/rooms/{id}               # Récupérer une salle
GET    /api/rooms                    # Lister toutes les salles
PUT    /api/rooms/{id}               # Modifier une salle
DELETE /api/rooms/{id}               # Supprimer une salle
```

### Réservations

```
POST   /api/reservations                              # Créer une réservation
GET    /api/reservations/{id}                         # Récupérer une réservation
GET    /api/reservations                              # Lister toutes les réservations
GET    /api/reservations/room/{roomId}                # Réservations actives d'une salle
GET    /api/reservations/room/{roomId}/availability   # Vérifier disponibilité
POST   /api/reservations/{id}/participants            # Ajouter un participant
DELETE /api/reservations/{id}/participants/{pid}      # Retirer un participant
PUT    /api/reservations/{id}/cancel                  # Annuler une réservation
DELETE /api/reservations/{id}                         # Supprimer une réservation
```

---

**Version** : 1.0  
**Date** : Février 2026
