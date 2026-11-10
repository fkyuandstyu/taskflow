package com.taskflow.service;

import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.dto.MoveTaskRequest;
import com.taskflow.dto.UpdateTaskRequest;
import com.taskflow.model.Task;
import com.taskflow.model.TaskList;
import com.taskflow.repository.TaskListRepository;
import com.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;

    // 在指定列表中建立卡片
    @Transactional
    public Task createTask(Long listId, CreateTaskRequest request) {
        TaskList taskList = taskListRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("找不到任務列表 ID: " + listId));

        // 計算新卡片的排序位置 (排在最底下)
        double nextPosition = taskRepository.findTopByTaskListIdOrderByPositionDesc(listId)
                .map(t -> t.getPosition() + 65536.0)
                .orElse(65536.0);

        Task task = Task.builder()
                .taskList(taskList)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .position(nextPosition)
                .build();

        return taskRepository.save(task);
    }

    // 更新卡片內容 (只更新有傳入的欄位)
    @Transactional
    public Task updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("找不到任務卡片 ID: " + taskId));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        return taskRepository.save(task);
    }

    // 核心亮點：拖拉移動卡片 (可跨列表移動，並更新 position 排序)
    @Transactional
    public Task moveTask(Long taskId, MoveTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("找不到任務卡片 ID: " + taskId));

        // 如果移動到了不同的列表，更新所屬列表
        if (!task.getTaskList().getId().equals(request.getTargetListId())) {
            TaskList targetList = taskListRepository.findById(request.getTargetListId())
                    .orElseThrow(() -> new IllegalArgumentException("找不到目標列表 ID: " + request.getTargetListId()));
            task.setTaskList(targetList);
        }

        // 更新排序權重
        task.setPosition(request.getNewPosition());

        return taskRepository.save(task);
    }

    // 刪除卡片
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("找不到任務卡片 ID: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }
}
