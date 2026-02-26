# Frontend React - Réservation de Salles

## 🚀 Démarrage Rapide

### 1. Démarrer le backend (dans un terminal)
```bash
cd /Users/yevheniibondarenko/api-reservation/api-reservation
mvn spring-boot:run
```

Le backend démarre sur **http://localhost:8080**

### 2. Démarrer le frontend (dans un autre terminal)
```bash
cd /Users/yevheniibondarenko/api-reservation/api-reservation/frontend
npm start
```

Le frontend démarre sur **http://localhost:3000**

## 📋 Fonctionnalités

### Onglet "Salles"
- ✅ Voir toutes les salles disponibles
- ✅ Créer une nouvelle salle (nom + capacité)
- ✅ Supprimer une salle

### Onglet "Nouvelle Réservation"
- ✅ Sélectionner une salle
- ✅ Choisir date/heure de début et fin
- ✅ Vérification automatique de disponibilité
- ✅ Ajouter des participants (nom + email)
- ✅ Validation de la capacité maximale
- ✅ Création de la réservation

### Onglet "Mes Réservations"
- ✅ Voir toutes les réservations
- ✅ Statut (Active / Annulée)
- ✅ Liste des participants
- ✅ Annuler une réservation
- ✅ Supprimer une réservation

## 🎨 Technologies Utilisées

- **React 19** - Framework frontend
- **Tailwind CSS** - Styling moderne
- **Axios** - Requêtes HTTP vers l'API
- **React Hooks** - useState, useEffect

## 📁 Structure du Code

```
frontend/
├── src/
│   ├── components/
│   │   ├── RoomList.js           # Gestion des salles
│   │   ├── ReservationForm.js    # Formulaire de réservation
│   │   └── ReservationList.js    # Liste des réservations
│   ├── services/
│   │   └── api.js                # Configuration Axios + API calls
│   ├── App.js                    # Composant principal avec navigation
│   └── index.css                 # Tailwind CSS
```

## 🔌 API Endpoints Utilisés

### Salles
- `GET /api/rooms` - Lister toutes les salles
- `POST /api/rooms` - Créer une salle
- `DELETE /api/rooms/{id}` - Supprimer une salle

### Réservations
- `GET /api/reservations` - Lister toutes les réservations
- `POST /api/reservations` - Créer une réservation
- `PUT /api/reservations/{id}/cancel` - Annuler une réservation
- `DELETE /api/reservations/{id}` - Supprimer une réservation
- `GET /api/reservations/room/{roomId}/availability` - Vérifier disponibilité

## 🎯 Exemple d'Utilisation

1. **Créer une salle** : Onglet "Salles" → "+ Nouvelle Salle" → Remplir le formulaire
2. **Faire une réservation** : Onglet "Nouvelle Réservation" → Sélectionner salle → Dates → Participants
3. **Voir les réservations** : Onglet "Mes Réservations"

## ⚠️ Notes Importantes

- Le backend doit être démarré **avant** le frontend
- CORS est configuré pour accepter les requêtes depuis `http://localhost:3000`
- Les warnings Tailwind CSS dans l'IDE sont normaux et n'affectent pas le fonctionnement

## 🐛 Dépannage

**Erreur CORS** : Vérifiez que le backend est bien démarré et que CorsConfig.java est présent

**Port 3000 déjà utilisé** :
```bash
lsof -ti:3000 | xargs kill -9
npm start
```

**Erreur de connexion à l'API** : Vérifiez que le backend tourne sur http://localhost:8080
