package com.taskflow.repository;

import com.taskflow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 任務卡片資料存取層
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 依據列表 ID 查詢該列表下的所有卡片，並依照 position 升冪排序
    List<Task> findByTaskListIdOrderByPositionAsc(Long listId);

    // 查詢某個列表中目前 position 最大的卡片 (新增卡片時方便計算預設排在最後面)
    Optional<Task> findTopByTaskListIdOrderByPositionDesc(Long listId);
}
