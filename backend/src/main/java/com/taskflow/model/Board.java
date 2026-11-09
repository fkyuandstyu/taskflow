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
 * 看板實體 (對應 MySQL 中的 boards 表)
 */
@Entity
@Table(name = "boards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 看板標題 (不可為空，長度上限 100)
    @Column(nullable = false, length = 100)
    private String title;

    // 看板描述
    @Column(columnDefinition = "TEXT")
    private String description;

    // 一對多關聯：一個看板有多個任務列表
    // mappedBy = "board" 表示 TaskList 裡面的 "board" 屬性才是這段關聯的擁有者 (外鍵所在處)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    @JsonIgnoreProperties("board")
    private List<TaskList> taskLists = new ArrayList<>();

    // 建立時間 (由 Hibernate 自動填入當下時間)
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 更新時間 (每次資料更新時自動更新)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
