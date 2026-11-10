package com.taskflow.service;

import com.taskflow.dto.CreateBoardRequest;
import com.taskflow.dto.CreateTaskListRequest;
import com.taskflow.model.Board;
import com.taskflow.model.TaskList;
import com.taskflow.repository.BoardRepository;
import com.taskflow.repository.TaskListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final TaskListRepository taskListRepository;

    // 取得所有看板 (最新建立排在前面)
    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }

    // 依 ID 取得單一看板詳細資料 (含底下的列表與卡片)
    @Transactional(readOnly = true)
    public Board getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到看板 ID: " + id));
    }

    // 建立新看板 (自動預設產生: 待處理、進行中、已完成 3 個列表)
    @Transactional
    public Board createBoard(CreateBoardRequest request) {
        Board board = Board.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        Board savedBoard = boardRepository.save(board);

        // 預設建立 3 個標準列表
        double basePosition = 65536.0;
        String[] defaultTitles = {"待處理 (Todo)", "進行中 (Doing)", "已完成 (Done)"};
        for (int i = 0; i < defaultTitles.length; i++) {
            TaskList list = TaskList.builder()
                    .board(savedBoard)
                    .title(defaultTitles[i])
                    .position(basePosition * (i + 1))
                    .build();
            taskListRepository.save(list);
            savedBoard.getTaskLists().add(list);
        }

        return savedBoard;
    }

    // 刪除看板
    @Transactional
    public void deleteBoard(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new IllegalArgumentException("找不到看板 ID: " + id);
        }
        boardRepository.deleteById(id);
    }

    // 在看板內手動新增自訂列表
    @Transactional
    public TaskList addListToBoard(Long boardId, CreateTaskListRequest request) {
        Board board = getBoardById(boardId);

        // 計算新列表 position (排在最後面)
        double nextPosition = taskListRepository.findTopByBoardIdOrderByPositionDesc(boardId)
                .map(l -> l.getPosition() + 65536.0)
                .orElse(65536.0);

        TaskList taskList = TaskList.builder()
                .board(board)
                .title(request.getTitle())
                .position(nextPosition)
                .build();

        return taskListRepository.save(taskList);
    }
}
