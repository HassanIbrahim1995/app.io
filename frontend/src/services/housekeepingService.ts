import api from './api';

export interface HousekeepingTask {
  id: number;
  roomId: number;
  roomNumber: string;
  assignedToId?: number;
  assignedToName?: string;
  taskType: string;
  title: string;
  description?: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'ON_HOLD';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  scheduledDate: string;
  startedAt?: string;
  completedAt?: string;
  estimatedDuration?: number;
  actualDuration?: number;
  notes?: string;
  completionNotes?: string;
  createdAt: string;
}

export interface HousekeepingTaskRequest {
  roomId: number;
  assignedToId?: number;
  taskType: string;
  title: string;
  description?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  scheduledDate: string;
  estimatedDuration?: number;
  notes?: string;
}

export const housekeepingService = {
  async getAllTasks(): Promise<HousekeepingTask[]> {
    const response = await api.get<HousekeepingTask[]>('/housekeeping/tasks');
    return response.data;
  },

  async getTaskById(id: number): Promise<HousekeepingTask> {
    const response = await api.get<HousekeepingTask>(`/housekeeping/tasks/${id}`);
    return response.data;
  },

  async getTasksByStatus(status: string): Promise<HousekeepingTask[]> {
    const response = await api.get<HousekeepingTask[]>(`/housekeeping/tasks/status/${status}`);
    return response.data;
  },

  async getTasksByRoom(roomId: number): Promise<HousekeepingTask[]> {
    const response = await api.get<HousekeepingTask[]>(`/housekeeping/tasks/room/${roomId}`);
    return response.data;
  },

  async getMyTasks(userId: number): Promise<HousekeepingTask[]> {
    const response = await api.get<HousekeepingTask[]>(`/housekeeping/tasks/assigned/${userId}`);
    return response.data;
  },

  async getTasksForDate(date: string): Promise<HousekeepingTask[]> {
    const response = await api.get<HousekeepingTask[]>(`/housekeeping/tasks/date/${date}`);
    return response.data;
  },

  async createTask(data: HousekeepingTaskRequest): Promise<HousekeepingTask> {
    const response = await api.post<HousekeepingTask>('/housekeeping/tasks', data);
    return response.data;
  },

  async assignTask(taskId: number, userId: number): Promise<HousekeepingTask> {
    const response = await api.post<HousekeepingTask>(
      `/housekeeping/tasks/${taskId}/assign/${userId}`
    );
    return response.data;
  },

  async startTask(taskId: number): Promise<HousekeepingTask> {
    const response = await api.post<HousekeepingTask>(`/housekeeping/tasks/${taskId}/start`);
    return response.data;
  },

  async completeTask(taskId: number, completionNotes?: string): Promise<HousekeepingTask> {
    const response = await api.post<HousekeepingTask>(
      `/housekeeping/tasks/${taskId}/complete`,
      null,
      { params: { completionNotes } }
    );
    return response.data;
  },

  async cancelTask(taskId: number, reason: string): Promise<HousekeepingTask> {
    const response = await api.post<HousekeepingTask>(
      `/housekeeping/tasks/${taskId}/cancel`,
      null,
      { params: { reason } }
    );
    return response.data;
  },

  async deleteTask(taskId: number): Promise<void> {
    await api.delete(`/housekeeping/tasks/${taskId}`);
  },
};
