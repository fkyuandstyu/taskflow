package com.taskflow.controller;

import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.dto.MoveTaskRequest;
import com.taskflow.dto.UpdateTaskRequest;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任務卡片模組 (Task)", description = "任務卡片 CRUD 與拖拉排序 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 允許前端跨域呼叫
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "在指定列表下建立任務卡片")
    @PostMapping("/lists/{listId}/tasks")
    public ResponseEntity<Task> createTask(
            @PathVariable Long listId,
            @Valid @RequestBody CreateTaskRequest request) {
        Task createdTask = taskService.createTask(listId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Operation(summary = "編輯修改任務卡片內容")
    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        Task updatedTask = taskService.updateTask(id, request);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "核心互動：拖拉移動卡片 (支援跨列表轉移與更新排序)")
    @PatchMapping("/tasks/{id}/move")
    public ResponseEntity<Task> moveTask(
            @PathVariable Long id,
            @Valid @RequestBody MoveTaskRequest request) {
        Task movedTask = taskService.moveTask(id, request);
        return ResponseEntity.ok(movedTask);
    }

    @Operation(summary = "刪除任務卡片")
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
