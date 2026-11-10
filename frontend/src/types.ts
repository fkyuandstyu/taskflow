export type Priority = 'LOW' | 'MEDIUM' | 'HIGH';

export interface Task {
  id: number;
  title: string;
  description: string | null;
  priority: Priority;
  dueDate: string | null;
  position: number;
  createdAt: string;
  updatedAt: string;
}

export interface TaskList {
  id: number;
  title: string;
  position: number;
  tasks: Task[];
}

export interface Board {
  id: number;
  title: string;
  description: string | null;
  taskLists: TaskList[];
  createdAt: string;
}

export interface CreateTaskPayload {
  title: string;
  description?: string;
  priority: Priority;
  dueDate?: string;
}

export interface CreateBoardPayload {
  title: string;
  description?: string;
}
