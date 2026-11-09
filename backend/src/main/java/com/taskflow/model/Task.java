package com.taskflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任務卡片實體 (對應 MySQL 中的 tasks 表)
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 任務所屬的列表 (多對一外鍵: list_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    @JsonIgnoreProperties("tasks")
    private TaskList taskList;

    // 任務標題
    @Column(nullable = false, length = 200)
    private String title;

    // 任務內容 / 詳細說明
    @Column(columnDefinition = "TEXT")
    private String description;

    // 優先級 (存入資料庫為字串: LOW / MEDIUM / HIGH)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    // 截止日期
    private LocalDate dueDate;

    // 卡片在列表中的排序權重 (浮點數，支援上下拖拉無縫排序)
    @Column(nullable = false)
    private Double position;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
