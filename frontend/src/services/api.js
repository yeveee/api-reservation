import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Rooms API
export const roomsAPI = {
  getAll: () => api.get('/rooms'),
  getById: (id) => api.get(`/rooms/${id}`),
  create: (room) => api.post('/rooms', room),
  update: (id, room) => api.put(`/rooms/${id}`, room),
  delete: (id) => api.delete(`/rooms/${id}`),
};

// Reservations API
export const reservationsAPI = {
  getAll: () => api.get('/reservations'),
  getById: (id) => api.get(`/reservations/${id}`),
  getByRoomId: (roomId) => api.get(`/reservations/room/${roomId}`),
  create: (reservation) => api.post('/reservations', reservation),
  cancel: (id) => api.put(`/reservations/${id}/cancel`),
  delete: (id) => api.delete(`/reservations/${id}`),
  addParticipant: (id, participant) => api.post(`/reservations/${id}/participants`, participant),
  removeParticipant: (reservationId, participantId) => 
    api.delete(`/reservations/${reservationId}/participants/${participantId}`),
  checkAvailability: (roomId, startTime, endTime) => 
    api.get(`/reservations/room/${roomId}/availability`, {
      params: { startTime, endTime }
    }),
};

export default api;
