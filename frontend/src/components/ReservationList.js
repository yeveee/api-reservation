import React, { useState, useEffect } from 'react';
import { reservationsAPI } from '../services/api';

function ReservationList({ refresh }) {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchReservations();
  }, [refresh]);

  const fetchReservations = async () => {
    try {
      setLoading(true);
      const response = await reservationsAPI.getAll();
      setReservations(response.data);
      setError(null);
    } catch (err) {
      setError('Erreur lors du chargement des réservations');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir annuler cette réservation ?')) {
      try {
        await reservationsAPI.cancel(id);
        fetchReservations();
      } catch (err) {
        alert('Erreur lors de l\'annulation');
        console.error(err);
      }
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette réservation ?')) {
      try {
        await reservationsAPI.delete(id);
        fetchReservations();
      } catch (err) {
        alert('Erreur lors de la suppression');
        console.error(err);
      }
    }
  };

  const formatDateTime = (dateTimeString) => {
    const date = new Date(dateTimeString);
    return date.toLocaleString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (loading) return <div className="text-center py-8">Chargement...</div>;
  if (error) return <div className="text-red-600 text-center py-8">{error}</div>;

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Réservations</h2>

      {reservations.length === 0 ? (
        <p className="text-gray-500 text-center py-8">Aucune réservation pour le moment.</p>
      ) : (
        <div className="space-y-4">
          {reservations.map((reservation) => (
            <div
              key={reservation.id}
              className={`border rounded-lg p-4 ${
                reservation.status === 'CANCELLED'
                  ? 'border-red-300 bg-red-50'
                  : 'border-gray-200 hover:shadow-md transition'
              }`}
            >
              <div className="flex justify-between items-start mb-3">
                <div>
                  <h3 className="text-lg font-semibold text-gray-800">
                    {reservation.room.name}
                  </h3>
                  <span
                    className={`inline-block px-2 py-1 text-xs rounded-full mt-1 ${
                      reservation.status === 'ACTIVE'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {reservation.status === 'ACTIVE' ? 'Active' : 'Annulée'}
                  </span>
                </div>
                {reservation.status === 'ACTIVE' && (
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleCancel(reservation.id)}
                      className="text-orange-600 hover:text-orange-800 text-sm"
                    >
                      Annuler
                    </button>
                    <button
                      onClick={() => handleDelete(reservation.id)}
                      className="text-red-600 hover:text-red-800 text-sm"
                    >
                      Supprimer
                    </button>
                  </div>
                )}
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm text-gray-600 mb-3">
                <div className="flex items-center">
                  <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span>Début: {formatDateTime(reservation.startTime)}</span>
                </div>
                <div className="flex items-center">
                  <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span>Fin: {formatDateTime(reservation.endTime)}</span>
                </div>
              </div>

              <div>
                <p className="text-sm font-medium text-gray-700 mb-2">
                  Participants ({reservation.participants.length}/{reservation.room.maxCapacity}):
                </p>
                <div className="flex flex-wrap gap-2">
                  {reservation.participants.map((participant) => (
                    <span
                      key={participant.id}
                      className="inline-flex items-center px-3 py-1 bg-blue-100 text-blue-800 text-xs rounded-full"
                    >
                      <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
                      </svg>
                      {participant.name}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ReservationList;
