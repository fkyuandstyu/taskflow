import axios from 'axios';
import type { Board, CreateBoardPayload, CreateTaskPayload, Task } from './types';

const api = axios.create({
  baseURL: '/api'
});

export const boardApi = {
  // 取得所有看板
  getAll: async (): Promise<Board[]> => {
    const res = await api.get('/boards');
    return res.data;
  },

  // 取得單一看板詳情 (含列表與卡片)
  getById: async (id: number): Promise<Board> => {
    const res = await api.get(`/boards/${id}`);
    return res.data;
  },

  // 建立看板
  create: async (payload: CreateBoardPayload): Promise<Board> => {
    const res = await api.post('/boards', payload);
    return res.data;
  },

  // 刪除看板
  delete: async (id: number): Promise<void> => {
    await api.delete(`/boards/${id}`);
  }
};

export const taskApi = {
  // 在指定列表建立卡片
  create: async (listId: number, payload: CreateTaskPayload): Promise<Task> => {
    const res = await api.post(`/lists/${listId}/tasks`, payload);
    return res.data;
  },

  // 拖拉移動卡片
  move: async (taskId: number, targetListId: number, newPosition: number): Promise<Task> => {
    const res = await api.patch(`/tasks/${taskId}/move`, {
      targetListId,
      newPosition
    });
    return res.data;
  },

  // 刪除卡片
  delete: async (taskId: number): Promise<void> => {
    await api.delete(`/tasks/${taskId}`);
  }
};
