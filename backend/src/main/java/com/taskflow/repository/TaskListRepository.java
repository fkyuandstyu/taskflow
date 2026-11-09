package com.taskflow.repository;

import com.taskflow.model.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 任務列表資料存取層
 */
@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {

    // 依據看板 ID 查詢該看板下的所有列表，並依照 position 升冪排序
    List<TaskList> findByBoardIdOrderByPositionAsc(Long boardId);

    // 查詢某個看板中目前 position 最大的列表 (新增列表時方便計算預設排在最後面)
    Optional<TaskList> findTopByBoardIdOrderByPositionDesc(Long boardId);
}
