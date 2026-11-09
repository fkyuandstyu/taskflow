package com.taskflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 任務列表實體 (對應 MySQL 中的 task_lists 表，如: Todo / Doing / Done)
 */
@Entity
@Table(name = "task_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 列表所屬的看板 (多對一外鍵: board_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    @JsonIgnoreProperties("taskLists")
    private Board board;

    // 列表標題 (例如 "待處理", "進行中", "已完成")
    @Column(nullable = false, length = 100)
    private String title;

    // 排序權重 (浮點數，支援無縫插入排序)
    @Column(nullable = false)
    private Double position;

    // 一對多關聯：一個列表內有多張任務卡片
    @OneToMany(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    @JsonIgnoreProperties("taskList")
    private List<Task> tasks = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
