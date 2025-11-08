import api from './api';
import { Room, RoomSearchParams } from '../types';

export const roomService = {
  async getAllRooms(): Promise<Room[]> {
    const response = await api.get<Room[]>('/rooms');
    return response.data;
  },

  async getRoomById(id: number): Promise<Room> {
    const response = await api.get<Room>(`/rooms/${id}`);
    return response.data;
  },

  async searchRooms(params: RoomSearchParams): Promise<Room[]> {
    const response = await api.get<Room[]>('/rooms/search', { params });
    return response.data;
  },
};
