package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 建立任務列表請求 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskListRequest {

    @NotBlank(message = "列表標題不能為空")
    @Size(max = 100, message = "列表標題長度不能超過 100 字")
    private String title;
}
