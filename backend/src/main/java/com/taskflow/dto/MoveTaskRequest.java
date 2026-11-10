package com.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 拖拉移動卡片請求 DTO (看板核心互動)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveTaskRequest {

    // 目標列表 ID (移入哪一個列表)
    @NotNull(message = "目標列表 ID 不能為空")
    private Long targetListId;

    // 新的排序位置 (浮點數權重)
    @NotNull(message = "新排序位置不能為空")
    private Double newPosition;
}
