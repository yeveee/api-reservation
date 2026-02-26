import React, { useState } from 'react';
import RoomList from './components/RoomList';
import ReservationForm from './components/ReservationForm';
import ReservationList from './components/ReservationList';

function App() {
  const [activeTab, setActiveTab] = useState('rooms');
  const [refreshKey, setRefreshKey] = useState(0);

  const handleReservationSuccess = () => {
    setRefreshKey(prev => prev + 1);
    setActiveTab('reservations');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <nav className="bg-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-2xl font-bold text-gray-800">
                🏋️ Réservation de Salles de Sport
              </h1>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8">
              <button
                onClick={() => setActiveTab('rooms')}
                className={`${
                  activeTab === 'rooms'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm transition`}
              >
                Salles
              </button>
              <button
                onClick={() => setActiveTab('create')}
                className={`${
                  activeTab === 'create'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm transition`}
              >
                Nouvelle Réservation
              </button>
              <button
                onClick={() => setActiveTab('reservations')}
                className={`${
                  activeTab === 'reservations'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm transition`}
              >
                Mes Réservations
              </button>
            </nav>
          </div>
        </div>

        <div className="mt-6">
          {activeTab === 'rooms' && <RoomList />}
          {activeTab === 'create' && <ReservationForm onSuccess={handleReservationSuccess} />}
          {activeTab === 'reservations' && <ReservationList refresh={refreshKey} />}
        </div>
      </div>

      <footer className="mt-12 bg-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <p className="text-center text-gray-500 text-sm">
            API Réservation de Salles - Architecture Hexagonale + DDD
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;
