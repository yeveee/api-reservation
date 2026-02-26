import React, { useState, useEffect } from 'react';
import { roomsAPI, reservationsAPI } from '../services/api';

function ReservationForm({ onSuccess }) {
  const [rooms, setRooms] = useState([]);
  const [formData, setFormData] = useState({
    roomId: '',
    startTime: '',
    endTime: '',
    participants: [{ name: '', email: '' }],
  });
  const [loading, setLoading] = useState(false);
  const [availability, setAvailability] = useState(null);

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      const response = await roomsAPI.getAll();
      setRooms(response.data);
    } catch (err) {
      console.error('Erreur lors du chargement des salles', err);
    }
  };

  const checkAvailability = async () => {
    if (formData.roomId && formData.startTime && formData.endTime) {
      try {
        const response = await reservationsAPI.checkAvailability(
          formData.roomId,
          formData.startTime,
          formData.endTime
        );
        setAvailability(response.data);
      } catch (err) {
        console.error('Erreur lors de la vérification', err);
      }
    }
  };

  useEffect(() => {
    checkAvailability();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formData.roomId, formData.startTime, formData.endTime]);

  const handleAddParticipant = () => {
    setFormData({
      ...formData,
      participants: [...formData.participants, { name: '', email: '' }],
    });
  };

  const handleRemoveParticipant = (index) => {
    const newParticipants = formData.participants.filter((_, i) => i !== index);
    setFormData({ ...formData, participants: newParticipants });
  };

  const handleParticipantChange = (index, field, value) => {
    const newParticipants = [...formData.participants];
    newParticipants[index][field] = value;
    setFormData({ ...formData, participants: newParticipants });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await reservationsAPI.create(formData);
      alert('Réservation créée avec succès !');
      setFormData({
        roomId: '',
        startTime: '',
        endTime: '',
        participants: [{ name: '', email: '' }],
      });
      setAvailability(null);
      if (onSuccess) onSuccess();
    } catch (err) {
      alert('Erreur lors de la création de la réservation: ' + (err.response?.data?.message || err.message));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const selectedRoom = rooms.find(r => r.id === formData.roomId);

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Nouvelle Réservation</h2>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Salle
          </label>
          <select
            required
            value={formData.roomId}
            onChange={(e) => setFormData({ ...formData, roomId: e.target.value })}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">Sélectionnez une salle</option>
            {rooms.map((room) => (
              <option key={room.id} value={room.id}>
                {room.name} (Capacité: {room.maxCapacity})
              </option>
            ))}
          </select>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Date et heure de début
            </label>
            <input
              type="datetime-local"
              required
              value={formData.startTime}
              onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Date et heure de fin
            </label>
            <input
              type="datetime-local"
              required
              value={formData.endTime}
              onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>

        {availability !== null && (
          <div className={`p-4 rounded-lg ${availability ? 'bg-green-50 text-green-800' : 'bg-red-50 text-red-800'}`}>
            {availability ? '✓ Salle disponible' : '✗ Salle non disponible pour cette période'}
          </div>
        )}

        <div>
          <div className="flex justify-between items-center mb-3">
            <label className="block text-sm font-medium text-gray-700">
              Participants {selectedRoom && `(Max: ${selectedRoom.maxCapacity})`}
            </label>
            <button
              type="button"
              onClick={handleAddParticipant}
              disabled={selectedRoom && formData.participants.length >= selectedRoom.maxCapacity}
              className="text-blue-600 hover:text-blue-800 text-sm disabled:text-gray-400"
            >
              + Ajouter un participant
            </button>
          </div>

          {formData.participants.map((participant, index) => (
            <div key={index} className="flex gap-3 mb-3">
              <input
                type="text"
                required
                placeholder="Nom"
                value={participant.name}
                onChange={(e) => handleParticipantChange(index, 'name', e.target.value)}
                className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              <input
                type="email"
                required
                placeholder="Email"
                value={participant.email}
                onChange={(e) => handleParticipantChange(index, 'email', e.target.value)}
                className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              {formData.participants.length > 1 && (
                <button
                  type="button"
                  onClick={() => handleRemoveParticipant(index)}
                  className="text-red-600 hover:text-red-800 px-3"
                >
                  ✕
                </button>
              )}
            </div>
          ))}
        </div>

        <button
          type="submit"
          disabled={loading || availability === false}
          className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white px-6 py-3 rounded-lg transition font-medium"
        >
          {loading ? 'Création en cours...' : 'Créer la réservation'}
        </button>
      </form>
    </div>
  );
}

export default ReservationForm;
