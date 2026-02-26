# DOCUMENTATION ACADÉMIQUE
## API de Réservation de Salles de Sport

### Projet Académique - Architecture Hexagonale & Domain-Driven Design

---

## Table des Matières

1. [Présentation du Projet](#1-présentation-du-projet)
2. [Analyse des Besoins](#2-analyse-des-besoins)
3. [Choix Architecturaux](#3-choix-architecturaux)
4. [Domain-Driven Design (DDD)](#4-domain-driven-design-ddd)
5. [Architecture Hexagonale](#5-architecture-hexagonale)
6. [Invariants Métier](#6-invariants-métier)
7. [Justification des Choix de Conception](#7-justification-des-choix-de-conception)
8. [Diagramme C4](#8-diagramme-c4)
9. [Conclusion](#9-conclusion)

---

## 1. Présentation du Projet

### 1.1 Contexte

Ce projet académique consiste en la réalisation d'une **API REST de réservation de salles de sport** développée avec **Java Spring Boot**. L'objectif principal est de démontrer la maîtrise des principes architecturaux modernes, notamment l'**architecture hexagonale** (Ports & Adapters) et le **Domain-Driven Design (DDD)**.

### 1.2 Objectifs Pédagogiques

- Appliquer les principes du Domain-Driven Design
- Implémenter une architecture hexagonale complète
- Séparer clairement les responsabilités en couches
- Protéger les invariants métier au niveau du domaine
- Garantir l'indépendance du domaine vis-à-vis de l'infrastructure

### 1.3 Technologies Utilisées

| Technologie | Version | Rôle |
|------------|---------|------|
| Java | 17 | Langage de programmation |
| Spring Boot | 3.2.0 | Framework applicatif |
| Spring Data JPA | 3.2.0 | Couche de persistence |
| H2 Database | 2.x | Base de données en mémoire |
| Maven | 3.6+ | Gestion des dépendances |

---

## 2. Analyse des Besoins

### 2.1 Besoins Fonctionnels

#### Gestion des Salles
- **RF1** : Créer une salle avec un nom et une capacité maximale
- **RF2** : Consulter les informations d'une salle
- **RF3** : Modifier les caractéristiques d'une salle
- **RF4** : Supprimer une salle
- **RF5** : Lister toutes les salles disponibles

#### Gestion des Réservations
- **RF6** : Créer une réservation pour une salle sur un créneau donné
- **RF7** : Ajouter des participants à une réservation
- **RF8** : Retirer des participants d'une réservation
- **RF9** : Annuler une réservation
- **RF10** : Vérifier la disponibilité d'une salle sur une période
- **RF11** : Consulter les réservations actives d'une salle

### 2.2 Règles Métier Critiques

1. **Éviter les doubles réservations** : Une salle ne peut pas être réservée simultanément sur des périodes qui se chevauchent
2. **Respect de la capacité** : Le nombre de participants ne peut jamais dépasser la capacité maximale de la salle
3. **Cohérence temporelle** : La date de début d'une réservation doit être antérieure à la date de fin
4. **Immutabilité des réservations annulées** : Une réservation annulée ne peut plus être modifiée
5. **Validation des périodes** : Les réservations ne peuvent pas être créées dans le passé

### 2.3 Contraintes Non-Fonctionnelles

- **Maintenabilité** : Code structuré et facilement évolutif
- **Testabilité** : Architecture permettant des tests unitaires isolés
- **Séparation des préoccupations** : Indépendance entre les couches
- **Cohérence des données** : Garantie de l'intégrité référentielle

---

## 3. Choix Architecturaux

### 3.1 Architecture en Couches

L'application est structurée en **quatre couches distinctes**, chacune ayant des responsabilités clairement définies :

```
┌─────────────────────────────────────────┐
│        Interfaces Layer (API)           │  ← Exposition HTTP
├─────────────────────────────────────────┤
│        Application Layer                │  ← Orchestration
├─────────────────────────────────────────┤
│        Domain Layer (Cœur Métier)       │  ← Logique métier
├─────────────────────────────────────────┤
│        Infrastructure Layer             │  ← Persistence, Adapters
└─────────────────────────────────────────┘
```

#### 3.1.1 Domain Layer (Couche Domaine)

**Responsabilité** : Contenir toute la logique métier et les règles de gestion.

**Composants** :
- **Aggregates** : `Reservation` (Aggregate Root)
- **Entities** : `Room`, `Participant`
- **Value Objects** : `ReservationPeriod`
- **Repository Interfaces** : `ReservationRepository`, `RoomRepository`

**Principe clé** : Cette couche ne dépend d'aucune autre couche. Elle définit ses propres interfaces (ports) que l'infrastructure implémente.

#### 3.1.2 Application Layer (Couche Application)

**Responsabilité** : Orchestrer les cas d'usage et coordonner les opérations métier.

**Composants** :
- `ReservationService` : Gestion des réservations
- `RoomService` : Gestion des salles

**Principe clé** : Cette couche ne contient pas de logique métier, elle coordonne uniquement les appels au domaine.

#### 3.1.3 Infrastructure Layer (Couche Infrastructure)

**Responsabilité** : Implémenter les détails techniques (persistence, communication externe).

**Composants** :
- **JPA Entities** : Modèle de persistence
- **Repository Implementations** : Adaptateurs implémentant les ports du domaine
- **Mappers** : Conversion entre modèle domaine et modèle JPA

**Principe clé** : Cette couche dépend du domaine mais le domaine ne dépend pas d'elle.

#### 3.1.4 Interfaces Layer (Couche Interfaces)

**Responsabilité** : Exposer l'API REST et gérer les interactions HTTP.

**Composants** :
- **Controllers** : `ReservationController`, `RoomController`
- **DTOs** : Objets de transfert de données
- **DTO Mappers** : Conversion entre domaine et DTOs

**Principe clé** : Point d'entrée de l'application, traduit les requêtes HTTP en appels métier.

### 3.2 Flux de Dépendances

```
Interfaces → Application → Domain ← Infrastructure
```

**Règle fondamentale** : Les dépendances pointent toujours vers le domaine, jamais l'inverse.

---

## 4. Domain-Driven Design (DDD)

### 4.1 Principes DDD Appliqués

Le Domain-Driven Design est une approche de conception logicielle qui place le **domaine métier au centre** de l'architecture.

#### 4.1.1 Ubiquitous Language (Langage Omniprésent)

Nous utilisons un vocabulaire métier cohérent dans tout le code :
- **Reservation** (Réservation)
- **Room** (Salle)
- **Participant** (Participant)
- **ReservationPeriod** (Période de réservation)
- **Capacity** (Capacité)

Ce langage est partagé entre les développeurs et les experts métier.

#### 4.1.2 Bounded Context (Contexte Délimité)

**Contexte** : `Reservation Context`

Ce contexte englobe tout ce qui concerne la réservation de salles de sport. Il est autonome et possède son propre modèle.

### 4.2 Building Blocks DDD

#### 4.2.1 Aggregate Root : Reservation

**Définition** : Un Aggregate est un cluster d'objets du domaine traités comme une unité pour les modifications de données.

```java
public class Reservation {
    private final String id;
    private final Room room;
    private final ReservationPeriod period;
    private final List<Participant> participants;
    private ReservationStatus status;
    
    // Méthodes métier
    public void addParticipant(Participant participant) { ... }
    public void removeParticipant(String participantId) { ... }
    public void cancel() { ... }
    public boolean isFull() { ... }
}
```

**Pourquoi Reservation est l'Aggregate Root ?**

1. **Cohérence transactionnelle** : Toutes les modifications des participants passent par la réservation
2. **Protection des invariants** : La réservation contrôle que la capacité n'est pas dépassée
3. **Cycle de vie** : Les participants n'existent que dans le contexte d'une réservation
4. **Frontière de cohérence** : Une réservation et ses participants forment une unité atomique

#### 4.2.2 Entity : Room

```java
public class Room {
    private final String id;
    private String name;
    private int maxCapacity;
}
```

**Caractéristiques** :
- Possède une identité unique (`id`)
- Peut être modifiée (nom, capacité)
- Existe indépendamment des réservations

#### 4.2.3 Entity : Participant

```java
public class Participant {
    private final String id;
    private String name;
    private String email;
}
```

**Caractéristiques** :
- Entity interne à l'Aggregate `Reservation`
- Ne peut être accédée que via la réservation
- Son cycle de vie est lié à la réservation

#### 4.2.4 Value Object : ReservationPeriod

```java
public class ReservationPeriod {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    
    public boolean overlapsWith(ReservationPeriod other) { ... }
}
```

**Caractéristiques** :
- **Immuable** : Une fois créé, ne peut être modifié
- **Sans identité** : Défini uniquement par ses attributs
- **Validé à la construction** : Garantit que `startTime < endTime`
- **Comportement métier** : Méthode `overlapsWith()` pour détecter les chevauchements

#### 4.2.5 Repository (Port)

```java
public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(String id);
    List<Reservation> findActiveReservationsByRoomIdAndPeriod(
        String roomId, ReservationPeriod period
    );
}
```

**Principe** : Le domaine définit le contrat, l'infrastructure l'implémente.

### 4.3 Modèle Riche vs Modèle Anémique

Notre implémentation utilise un **modèle riche** où les objets du domaine contiennent à la fois les données ET le comportement.

**Exemple de modèle riche** :

```java
// ✅ BON : La logique métier est dans le domaine
reservation.addParticipant(participant);  // Vérifie la capacité

// ❌ MAUVAIS : Modèle anémique
reservation.getParticipants().add(participant);  // Pas de validation
```

---

## 5. Architecture Hexagonale

### 5.1 Principes de l'Architecture Hexagonale

L'architecture hexagonale, également appelée **Ports & Adapters**, vise à :

1. **Isoler le domaine** des détails techniques
2. **Inverser les dépendances** : l'infrastructure dépend du domaine
3. **Faciliter les tests** : le domaine peut être testé sans infrastructure
4. **Permettre la substitution** : changer de base de données sans toucher au domaine

### 5.2 Ports et Adapters

#### 5.2.1 Ports (Interfaces)

Les **ports** sont des interfaces définies dans le domaine :

```java
// Port défini dans le domaine
package com.reservation.domain.repositories;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(String id);
}
```

#### 5.2.2 Adapters (Implémentations)

Les **adapters** sont des implémentations dans l'infrastructure :

```java
// Adapter implémenté dans l'infrastructure
package com.reservation.infrastructure.persistence.adapters;

@Component
public class ReservationRepositoryImpl implements ReservationRepository {
    private final SpringDataReservationRepository springDataRepository;
    
    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity jpaEntity = ReservationMapper.toJpaEntity(reservation);
        ReservationJpaEntity saved = springDataRepository.save(jpaEntity);
        return ReservationMapper.toDomain(saved);
    }
}
```

### 5.3 Séparation Domaine / Infrastructure

#### 5.3.1 Modèle du Domaine

```java
// Modèle pur, sans annotations JPA
public class Reservation {
    private final String id;
    private final Room room;
    private final ReservationPeriod period;
}
```

#### 5.3.2 Modèle de Persistence

```java
// Modèle JPA séparé
@Entity
@Table(name = "reservations")
public class ReservationJpaEntity {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomJpaEntity room;
}
```

#### 5.3.3 Mappers (Anti-Corruption Layer)

Les mappers protègent le domaine de la "contamination" par l'infrastructure :

```java
public class ReservationMapper {
    public static ReservationJpaEntity toJpaEntity(Reservation reservation) {
        // Conversion Domaine → JPA
    }
    
    public static Reservation toDomain(ReservationJpaEntity jpaEntity) {
        // Conversion JPA → Domaine
    }
}
```

### 5.4 Avantages de cette Approche

1. **Testabilité** : Le domaine peut être testé sans base de données
2. **Maintenabilité** : Changements d'infrastructure sans impact sur le métier
3. **Clarté** : Séparation nette entre logique métier et détails techniques
4. **Évolutivité** : Facile d'ajouter de nouveaux adapters (REST, GraphQL, etc.)

---

## 6. Invariants Métier

Les **invariants** sont des règles métier qui doivent **toujours** être vraies. Ils sont protégés par l'Aggregate Root.

### 6.1 Éviter les Doubles Réservations

#### 6.1.1 Problème

Une salle ne peut pas être réservée sur deux périodes qui se chevauchent.

#### 6.1.2 Solution Implémentée

**Niveau 1 : Value Object `ReservationPeriod`**

```java
public boolean overlapsWith(ReservationPeriod other) {
    return this.startTime.isBefore(other.endTime) && 
           this.endTime.isAfter(other.startTime);
}
```

**Niveau 2 : Service Application**

```java
private void checkForConflictingReservations(String roomId, ReservationPeriod period) {
    List<Reservation> existingReservations = 
        reservationRepository.findActiveReservationsByRoomIdAndPeriod(roomId, period);
    
    for (Reservation existing : existingReservations) {
        if (existing.overlapsWith(period)) {
            throw new IllegalStateException("La salle est déjà réservée sur cette période");
        }
    }
}
```

**Niveau 3 : Requête SQL Optimisée**

```java
@Query("SELECT r FROM ReservationJpaEntity r WHERE r.room.id = :roomId " +
       "AND r.status = 'ACTIVE' " +
       "AND r.startTime < :endTime " +
       "AND r.endTime > :startTime")
List<ReservationJpaEntity> findActiveReservationsByRoomIdAndPeriod(...);
```

#### 6.1.3 Garanties

- ✅ Vérification avant création de réservation
- ✅ Requête optimisée pour détecter les chevauchements
- ✅ Exception levée si conflit détecté

### 6.2 Contrôle de la Capacité Maximale

#### 6.2.1 Problème

Le nombre de participants ne doit jamais dépasser la capacité de la salle.

#### 6.2.2 Solution Implémentée

**À la création** :

```java
public static Reservation create(Room room, ReservationPeriod period, 
                                List<Participant> initialParticipants) {
    if (initialParticipants.size() > room.getMaxCapacity()) {
        throw new IllegalStateException(
            "Le nombre de participants dépasse la capacité de la salle"
        );
    }
    return new Reservation(...);
}
```

**À l'ajout d'un participant** :

```java
public void addParticipant(Participant participant) {
    ensureNotCancelled();
    
    if (isFull()) {
        throw new IllegalStateException(
            "Impossible d'ajouter un participant : la salle est pleine"
        );
    }
    
    participants.add(participant);
}

public boolean isFull() {
    return participants.size() >= room.getMaxCapacity();
}
```

#### 6.2.3 Garanties

- ✅ Validation à la création
- ✅ Validation à chaque ajout de participant
- ✅ Impossible de contourner la règle (encapsulation)

### 6.3 Protection des Réservations Annulées

#### 6.3.1 Problème

Une réservation annulée ne doit plus pouvoir être modifiée.

#### 6.3.2 Solution Implémentée

```java
public void addParticipant(Participant participant) {
    ensureNotCancelled();  // ← Vérification systématique
    // ...
}

public void removeParticipant(String participantId) {
    ensureNotCancelled();  // ← Vérification systématique
    // ...
}

private void ensureNotCancelled() {
    if (status == ReservationStatus.CANCELLED) {
        throw new IllegalStateException(
            "Impossible de modifier une réservation annulée"
        );
    }
}
```

#### 6.3.3 Garanties

- ✅ Toute modification vérifie d'abord le statut
- ✅ Exception explicite si tentative de modification
- ✅ Transition d'état unidirectionnelle (ACTIVE → CANCELLED)

### 6.4 Validation des Périodes

#### 6.4.1 Problème

- La date de début doit être avant la date de fin
- Les réservations ne peuvent pas être dans le passé

#### 6.4.2 Solution Implémentée

```java
private void validatePeriod(LocalDateTime start, LocalDateTime end) {
    if (start == null || end == null) {
        throw new IllegalArgumentException(
            "Les dates ne peuvent pas être nulles"
        );
    }
    if (!start.isBefore(end)) {
        throw new IllegalArgumentException(
            "La date de début doit être antérieure à la date de fin"
        );
    }
    if (start.isBefore(LocalDateTime.now())) {
        throw new IllegalArgumentException(
            "La date de début ne peut pas être dans le passé"
        );
    }
}
```

#### 6.4.3 Garanties

- ✅ Validation au constructeur du Value Object
- ✅ Impossible de créer une période invalide
- ✅ Immutabilité garantit que la période reste valide

### 6.5 Pourquoi Reservation est l'Aggregate Root

| Critère | Justification |
|---------|---------------|
| **Cohérence transactionnelle** | Toutes les modifications des participants passent par la réservation |
| **Protection des invariants** | La réservation vérifie la capacité, le statut, etc. |
| **Frontière de cohérence** | Une réservation + ses participants = une unité atomique |
| **Cycle de vie** | Les participants n'existent que dans le contexte d'une réservation |
| **Point d'accès unique** | Impossible de modifier un participant sans passer par la réservation |

---

## 7. Justification des Choix de Conception

### 7.1 Pourquoi l'Architecture Hexagonale ?

#### 7.1.1 Indépendance du Domaine

**Problème** : Dans une architecture traditionnelle, le domaine dépend souvent de l'infrastructure (annotations JPA, etc.).

**Solution** : Le domaine est pur, sans dépendances techniques.

```java
// ✅ Domaine pur
public class Reservation {
    private final String id;
    // Pas d'annotations JPA
}

// ✅ Infrastructure séparée
@Entity
public class ReservationJpaEntity {
    @Id
    private String id;
}
```

#### 7.1.2 Testabilité

Le domaine peut être testé unitairement sans base de données :

```java
@Test
void shouldNotAllowAddingParticipantWhenFull() {
    Room room = new Room("Salle A", 2);
    List<Participant> participants = Arrays.asList(
        new Participant("Alice", "alice@test.com"),
        new Participant("Bob", "bob@test.com")
    );
    
    Reservation reservation = Reservation.create(room, period, participants);
    
    assertThrows(IllegalStateException.class, () -> {
        reservation.addParticipant(new Participant("Charlie", "charlie@test.com"));
    });
}
```

### 7.2 Pourquoi le Domain-Driven Design ?

#### 7.2.1 Complexité Métier

Les règles métier (capacité, chevauchements, annulations) sont complexes et méritent d'être au centre de l'architecture.

#### 7.2.2 Évolutivité

Ajouter de nouvelles règles métier se fait dans le domaine, sans toucher à l'infrastructure.

#### 7.2.3 Communication

Le code reflète le langage métier, facilitant la communication avec les experts du domaine.

### 7.3 Pourquoi Séparer les Modèles (Domaine vs JPA) ?

#### 7.3.1 Principe de Responsabilité Unique

- **Modèle Domaine** : Représente les concepts métier
- **Modèle JPA** : Optimisé pour la persistence

#### 7.3.2 Flexibilité

Changer de base de données (PostgreSQL, MongoDB) n'impacte pas le domaine.

#### 7.3.3 Clarté

Le domaine n'est pas pollué par des annotations techniques.

### 7.4 Pourquoi des Value Objects ?

#### 7.4.1 Immutabilité

Un `ReservationPeriod` ne peut pas être modifié après création, garantissant la cohérence.

#### 7.4.2 Validation Centralisée

Toute la logique de validation des périodes est dans le Value Object.

#### 7.4.3 Comportement Métier

La méthode `overlapsWith()` encapsule une règle métier complexe.

### 7.5 Pourquoi des Factory Methods ?

```java
public static Reservation create(Room room, ReservationPeriod period, 
                                List<Participant> participants) {
    // Validation complète avant création
}
```

**Avantages** :
- Validation centralisée
- Constructeur privé empêche la création invalide
- Intention claire (create vs reconstitute)

---

## 8. Diagramme C4

### 8.1 Niveau 1 : Contexte Système

```
┌─────────────┐
│   Utilisateur│
│   (Client)  │
└──────┬──────┘
       │ HTTP/REST
       ▼
┌─────────────────────────────────┐
│  API Réservation Salles Sport  │
│  (Spring Boot Application)      │
└─────────────┬───────────────────┘
              │ JDBC
              ▼
       ┌──────────────┐
       │  H2 Database │
       └──────────────┘
```

**Description** : L'utilisateur interagit avec l'API REST qui persiste les données dans une base H2.

### 8.2 Niveau 2 : Conteneurs

```
┌────────────────────────────────────────────────┐
│         API Réservation (Spring Boot)          │
├────────────────────────────────────────────────┤
│                                                │
│  ┌──────────────────────────────────────┐     │
│  │     Interfaces Layer (REST API)      │     │
│  │  - ReservationController             │     │
│  │  - RoomController                    │     │
│  └────────────┬─────────────────────────┘     │
│               │                               │
│  ┌────────────▼─────────────────────────┐     │
│  │     Application Layer (Services)     │     │
│  │  - ReservationService                │     │
│  │  - RoomService                       │     │
│  └────────────┬─────────────────────────┘     │
│               │                               │
│  ┌────────────▼─────────────────────────┐     │
│  │     Domain Layer (Core Business)     │     │
│  │  - Reservation (Aggregate)           │     │
│  │  - Room, Participant (Entities)      │     │
│  │  - ReservationPeriod (Value Object)  │     │
│  └────────────┬─────────────────────────┘     │
│               │                               │
│  ┌────────────▼─────────────────────────┐     │
│  │  Infrastructure Layer (Persistence)  │     │
│  │  - JPA Entities                      │     │
│  │  - Repository Implementations        │     │
│  └──────────────────────────────────────┘     │
│                                                │
└────────────────────────────────────────────────┘
```

### 8.3 Niveau 3 : Composants (Aggregate Reservation)

```
┌─────────────────────────────────────────────┐
│         Reservation (Aggregate Root)        │
├─────────────────────────────────────────────┤
│                                             │
│  + create(room, period, participants)       │
│  + addParticipant(participant)              │
│  + removeParticipant(participantId)         │
│  + cancel()                                 │
│  + isFull(): boolean                        │
│  + overlapsWith(period): boolean            │
│                                             │
│  - ensureNotCancelled()                     │
│  - validateCreation()                       │
│                                             │
├─────────────────────────────────────────────┤
│  Invariants Protégés:                       │
│  ✓ Capacité maximale respectée              │
│  ✓ Réservations annulées non modifiables    │
│  ✓ Participants uniques                     │
└─────────────────────────────────────────────┘
         │                    │
         │ contient           │ utilise
         ▼                    ▼
┌──────────────┐      ┌──────────────────┐
│ Participant  │      │ ReservationPeriod│
│  (Entity)    │      │  (Value Object)  │
└──────────────┘      └──────────────────┘
```

### 8.4 Flux de Dépendances Détaillé

```
┌─────────────────┐
│ REST Controller │
└────────┬────────┘
         │ utilise
         ▼
┌─────────────────┐
│ Application     │
│ Service         │
└────────┬────────┘
         │ orchestre
         ▼
┌─────────────────┐      ┌──────────────────┐
│ Domain          │◄─────│ Repository       │
│ (Aggregate)     │ impl │ (Infrastructure) │
└─────────────────┘      └──────────────────┘
         │
         │ définit
         ▼
┌─────────────────┐
│ Repository      │
│ Interface (Port)│
└─────────────────┘
```

**Principe** : Les flèches représentent les dépendances. Le domaine ne dépend de rien.

---

## 9. Conclusion

### 9.1 Objectifs Atteints

Ce projet démontre la maîtrise des concepts suivants :

✅ **Architecture Hexagonale**
- Séparation claire entre domaine et infrastructure
- Inversion de dépendances via les ports et adapters
- Domaine testable indépendamment

✅ **Domain-Driven Design**
- Aggregate Root bien défini (Reservation)
- Value Objects immuables (ReservationPeriod)
- Modèle riche avec comportement métier
- Ubiquitous Language respecté

✅ **Invariants Métier**
- Protection contre les doubles réservations
- Contrôle de la capacité maximale
- Validation des périodes
- Immutabilité des réservations annulées

✅ **Séparation des Responsabilités**
- 4 couches distinctes et cohérentes
- Chaque couche a un rôle précis
- Pas de fuite d'abstraction

### 9.2 Avantages de l'Architecture Choisie

| Avantage | Explication |
|----------|-------------|
| **Maintenabilité** | Changements localisés dans une seule couche |
| **Testabilité** | Domaine testable sans infrastructure |
| **Évolutivité** | Facile d'ajouter de nouvelles fonctionnalités |
| **Clarté** | Code qui reflète le métier |
| **Indépendance** | Changement de technologie sans impact sur le métier |

### 9.3 Points d'Amélioration Possibles

Pour aller plus loin, on pourrait :

1. **Ajouter des Events** : Domain Events pour notifier les changements
2. **CQRS** : Séparer les commandes des requêtes
3. **Spécifications** : Pattern Specification pour les requêtes complexes
4. **Validation avancée** : Bean Validation sur les DTOs
5. **Sécurité** : Authentification et autorisation
6. **Tests** : Tests unitaires et d'intégration complets

### 9.4 Leçons Apprises

1. **Le domaine d'abord** : Commencer par modéliser le métier avant la technique
2. **Les invariants sont cruciaux** : Ils garantissent la cohérence des données
3. **La séparation paie** : Même si cela demande plus de code initial
4. **Les Value Objects sont puissants** : Ils encapsulent validation et comportement
5. **L'Aggregate Root protège** : Il est le gardien de la cohérence

### 9.5 Conclusion Finale

Ce projet illustre comment une architecture bien pensée, basée sur les principes DDD et hexagonaux, permet de créer une application **maintenable**, **testable** et **évolutive**. 

La séparation stricte des responsabilités et la protection des invariants métier garantissent que le code reste **cohérent** et **fiable** même lors de l'ajout de nouvelles fonctionnalités.

L'investissement initial dans une architecture solide se traduit par une **réduction significative** de la dette technique et une **facilité de maintenance** à long terme.

---

**Auteur** : Projet Académique  
**Date** : Février 2026  
**Version** : 1.0
