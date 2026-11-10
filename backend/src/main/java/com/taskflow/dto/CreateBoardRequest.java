package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 建立看板請求 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBoardRequest {

    @NotBlank(message = "看板標題不能為空")
    @Size(max = 100, message = "看板標題長度不能超過 100 字")
    private String title;

    private String description;
}
