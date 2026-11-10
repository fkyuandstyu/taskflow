package com.taskflow.dto;

import com.taskflow.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * 建立任務卡片請求 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskRequest {

    @NotBlank(message = "任務標題不能為空")
    @Size(max = 200, message = "任務標題長度不能超過 200 字")
    private String title;

    private String description;

    // 優先級，預設為 MEDIUM
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    // 截止日 (格式: YYYY-MM-DD)
    private LocalDate dueDate;
}
