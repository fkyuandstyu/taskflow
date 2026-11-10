package com.taskflow.controller;

import com.taskflow.dto.CreateBoardRequest;
import com.taskflow.dto.CreateTaskListRequest;
import com.taskflow.model.Board;
import com.taskflow.model.TaskList;
import com.taskflow.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "看板模組 (Board)", description = "看板與列表管理 API")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 允許前端跨域呼叫
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "取得所有看板列表")
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    @Operation(summary = "取得單一看板詳細資料 (包含其下所有列表與任務卡片)")
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @Operation(summary = "建立新看板 (自動預設產生 Todo/Doing/Done 三個列表)")
    @PostMapping
    public ResponseEntity<Board> createBoard(@Valid @RequestBody CreateBoardRequest request) {
        Board createdBoard = boardService.createBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
    }

    @Operation(summary = "在指定看板下新增任務列表")
    @PostMapping("/{boardId}/lists")
    public ResponseEntity<TaskList> addListToBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody CreateTaskListRequest request) {
        TaskList createdList = boardService.addListToBoard(boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdList);
    }

    @Operation(summary = "刪除看板")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
