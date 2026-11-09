package com.taskflow.repository;

import com.taskflow.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 看板資料存取層
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 依建立時間倒序查詢所有看板 (最新建立的排在最前面)
    List<Board> findAllByOrderByCreatedAtDesc();
}
