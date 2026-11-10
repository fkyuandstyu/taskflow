package com.taskflow.dto;

import com.taskflow.model.Priority;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * 更新任務卡片內容請求 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskRequest {

    @Size(max = 200, message = "任務標題長度不能超過 200 字")
    private String title;

    private String description;

    private Priority priority;

    private LocalDate dueDate;
}
