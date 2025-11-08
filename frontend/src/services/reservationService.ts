import api from './api';
import { Reservation, ReservationRequest } from '../types';

export const reservationService = {
  async createReservation(data: ReservationRequest): Promise<Reservation> {
    const response = await api.post<Reservation>('/reservations', data);
    return response.data;
  },

  async getReservationById(id: number): Promise<Reservation> {
    const response = await api.get<Reservation>(`/reservations/${id}`);
    return response.data;
  },

  async getMyReservations(): Promise<Reservation[]> {
    const response = await api.get<Reservation[]>('/reservations/my-reservations');
    return response.data;
  },

  async cancelReservation(id: number): Promise<Reservation> {
    const response = await api.post<Reservation>(`/reservations/${id}/cancel`);
    return response.data;
  },
};
