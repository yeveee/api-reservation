import React, { useState, useEffect } from 'react';
import { roomsAPI } from '../services/api';

function RoomList({ onSelectRoom }) {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [newRoom, setNewRoom] = useState({ name: '', maxCapacity: '' });

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      setLoading(true);
      const response = await roomsAPI.getAll();
      setRooms(response.data);
      setError(null);
    } catch (err) {
      setError('Erreur lors du chargement des salles');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRoom = async (e) => {
    e.preventDefault();
    try {
      await roomsAPI.create({
        name: newRoom.name,
        maxCapacity: parseInt(newRoom.maxCapacity),
      });
      setNewRoom({ name: '', maxCapacity: '' });
      setShowForm(false);
      fetchRooms();
    } catch (err) {
      alert('Erreur lors de la création de la salle');
      console.error(err);
    }
  };

  const handleDeleteRoom = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette salle ?')) {
      try {
        await roomsAPI.delete(id);
        fetchRooms();
      } catch (err) {
        alert('Erreur lors de la suppression');
        console.error(err);
      }
    }
  };

  if (loading) return <div className="text-center py-8">Chargement...</div>;
  if (error) return <div className="text-red-600 text-center py-8">{error}</div>;

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Salles de Sport</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition"
        >
          {showForm ? 'Annuler' : '+ Nouvelle Salle'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreateRoom} className="mb-6 bg-gray-50 p-4 rounded-lg">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nom de la salle
              </label>
              <input
                type="text"
                required
                value={newRoom.name}
                onChange={(e) => setNewRoom({ ...newRoom, name: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Salle de Yoga"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Capacité maximale
              </label>
              <input
                type="number"
                required
                min="1"
                value={newRoom.maxCapacity}
                onChange={(e) => setNewRoom({ ...newRoom, maxCapacity: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="20"
              />
            </div>
          </div>
          <button
            type="submit"
            className="mt-4 bg-green-600 hover:bg-green-700 text-white px-6 py-2 rounded-lg transition"
          >
            Créer la salle
          </button>
        </form>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {rooms.length === 0 ? (
          <p className="text-gray-500 col-span-full text-center py-8">
            Aucune salle disponible. Créez-en une !
          </p>
        ) : (
          rooms.map((room) => (
            <div
              key={room.id}
              className="border border-gray-200 rounded-lg p-4 hover:shadow-lg transition cursor-pointer"
              onClick={() => onSelectRoom && onSelectRoom(room)}
            >
              <h3 className="text-lg font-semibold text-gray-800 mb-2">{room.name}</h3>
              <div className="flex items-center text-gray-600 mb-3">
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <span>Capacité: {room.maxCapacity} personnes</span>
              </div>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleDeleteRoom(room.id);
                }}
                className="text-red-600 hover:text-red-800 text-sm"
              >
                Supprimer
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default RoomList;
