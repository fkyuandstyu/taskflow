import React, { useEffect, useState } from 'react';
import type { Board, Priority, Task } from './types';
import { boardApi, taskApi } from './api';
import { 
  Plus, 
  Calendar, 
  Trash2, 
  Layout, 
  AlertCircle, 
  Clock, 
  CheckCircle2, 
  Sparkles,
  ChevronRight
} from 'lucide-react';

export function App() {
  const [boards, setBoards] = useState<Board[]>([]);
  const [currentBoard, setCurrentBoard] = useState<Board | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // 新建卡片 Modal 狀態
  const [isNewTaskOpen, setIsNewTaskOpen] = useState(false);
  const [selectedListId, setSelectedListId] = useState<number | null>(null);
  const [taskTitle, setTaskTitle] = useState('');
  const [taskDesc, setTaskDesc] = useState('');
  const [taskPriority, setTaskPriority] = useState<Priority>('MEDIUM');
  const [taskDueDate, setTaskDueDate] = useState('');

  // 新建看板 Modal 狀態
  const [isNewBoardOpen, setIsNewBoardOpen] = useState(false);
  const [boardTitle, setBoardTitle] = useState('');
  const [boardDesc, setBoardDesc] = useState('');

  // 拖拉狀態
  const [draggedTaskId, setDraggedTaskId] = useState<number | null>(null);

  // 初始化取得看板
  const fetchBoards = async () => {
    try {
      setLoading(true);
      const list = await boardApi.getAll();
      setBoards(list);
      if (list.length > 0) {
        // 預設載入第一個看板的完整資訊
        const detail = await boardApi.getById(list[0].id);
        setCurrentBoard(detail);
      }
    } catch (err) {
      console.error('Failed to load boards', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBoards();
  }, []);

  const selectBoard = async (id: number) => {
    setLoading(true);
    try {
      const detail = await boardApi.getById(id);
      setCurrentBoard(detail);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateBoard = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!boardTitle.trim()) return;
    const newBoard = await boardApi.create({
      title: boardTitle,
      description: boardDesc
    });
    setBoardTitle('');
    setBoardDesc('');
    setIsNewBoardOpen(false);
    await fetchBoards();
    await selectBoard(newBoard.id);
  };

  const handleCreateTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedListId || !taskTitle.trim() || !currentBoard) return;

    await taskApi.create(selectedListId, {
      title: taskTitle,
      description: taskDesc || undefined,
      priority: taskPriority,
      dueDate: taskDueDate || undefined
    });

    setTaskTitle('');
    setTaskDesc('');
    setTaskDueDate('');
    setIsNewTaskOpen(false);
    // 重新載入當前看板
    const updated = await boardApi.getById(currentBoard.id);
    setCurrentBoard(updated);
  };

  const handleDeleteTask = async (taskId: number) => {
    if (!currentBoard) return;
    await taskApi.delete(taskId);
    const updated = await boardApi.getById(currentBoard.id);
    setCurrentBoard(updated);
  };

  // HTML5 原生拖拉：開始拖動
  const handleDragStart = (e: React.DragEvent, taskId: number) => {
    setDraggedTaskId(taskId);
    e.dataTransfer.setData('text/plain', taskId.toString());
  };

  // 允許放置
  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };

  // 放置到目標列表
  const handleDrop = async (e: React.DragEvent, targetListId: number) => {
    e.preventDefault();
    if (!draggedTaskId || !currentBoard) return;

    const targetList = currentBoard.taskLists.find(l => l.id === targetListId);
    if (!targetList) return;

    // 計算新排序 position (排在最後面)
    const maxPos = targetList.tasks.length > 0 
      ? Math.max(...targetList.tasks.map(t => t.position)) + 65536.0 
      : 65536.0;

    // 樂觀更新前端畫面
    const updatedLists = currentBoard.taskLists.map(list => {
      // 從原列表移除
      const filteredTasks = list.tasks.filter(t => t.id !== draggedTaskId);
      if (list.id === targetListId) {
        // 找到被拖動的卡片
        let movedTask: Task | undefined;
        for (const l of currentBoard.taskLists) {
          movedTask = l.tasks.find(t => t.id === draggedTaskId);
          if (movedTask) break;
        }
        if (movedTask) {
          return {
            ...list,
            tasks: [...filteredTasks, { ...movedTask, position: maxPos }]
          };
        }
      }
      return { ...list, tasks: filteredTasks };
    });

    setCurrentBoard({ ...currentBoard, taskLists: updatedLists });

    // 呼叫後端 API
    await taskApi.move(draggedTaskId, targetListId, maxPos);
    setDraggedTaskId(null);
  };

  const getPriorityBadge = (priority: Priority) => {
    switch (priority) {
      case 'HIGH':
        return <span className="px-2 py-0.5 rounded-full text-xs font-semibold bg-red-100 text-red-700 flex items-center gap-1"><AlertCircle size={12}/> 高</span>;
      case 'MEDIUM':
        return <span className="px-2 py-0.5 rounded-full text-xs font-semibold bg-amber-100 text-amber-700 flex items-center gap-1"><Clock size={12}/> 中</span>;
      case 'LOW':
        return <span className="px-2 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-700 flex items-center gap-1"><CheckCircle2 size={12}/> 低</span>;
    }
  };

  return (
    <div className="flex h-screen bg-slate-900 text-slate-100 font-sans">
      {/* 側邊導覽列 */}
      <aside className="w-64 bg-slate-950 border-r border-slate-800 flex flex-col">
        <div className="p-5 flex items-center gap-3 border-b border-slate-800">
          <div className="bg-indigo-600 p-2 rounded-xl text-white shadow-lg shadow-indigo-500/30">
            <Layout size={22} />
          </div>
          <div>
            <h1 className="font-bold text-lg text-white tracking-wide">TaskFlow</h1>
            <p className="text-xs text-slate-400">團隊敏捷協作平台</p>
          </div>
        </div>

        <div className="p-4 flex-1 overflow-y-auto">
          <div className="flex items-center justify-between text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">
            <span>我的看板</span>
            <button 
              onClick={() => setIsNewBoardOpen(true)}
              className="hover:text-indigo-400 transition"
              title="新增看板"
            >
              <Plus size={16} />
            </button>
          </div>

          <div className="space-y-1.5">
            {boards.map(b => (
              <button
                key={b.id}
                onClick={() => selectBoard(b.id)}
                className={`w-full text-left px-3.5 py-2.5 rounded-xl text-sm font-medium transition flex items-center justify-between group ${
                  currentBoard?.id === b.id 
                    ? 'bg-indigo-600/20 text-indigo-300 border border-indigo-500/30' 
                    : 'text-slate-400 hover:bg-slate-900 hover:text-slate-200'
                }`}
              >
                <span className="truncate">{b.title}</span>
                <ChevronRight size={14} className={`opacity-0 group-hover:opacity-100 transition ${currentBoard?.id === b.id ? 'opacity-100 text-indigo-400' : ''}`} />
              </button>
            ))}
          </div>
        </div>

        <div className="p-4 border-t border-slate-800/80 bg-slate-950/50">
          <div className="bg-indigo-950/40 border border-indigo-800/40 rounded-xl p-3 flex items-start gap-2.5">
            <Sparkles size={16} className="text-indigo-400 shrink-0 mt-0.5" />
            <p className="text-xs text-indigo-200/80 leading-relaxed">
              支援原生 HTML5 拖拉排序與跨欄移動，操作即時寫入 MySQL。
            </p>
          </div>
        </div>
      </aside>

      {/* 主畫面 */}
      <main className="flex-1 flex flex-col overflow-hidden bg-slate-900">
        {/* 頂部 Header */}
        <header className="h-16 border-b border-slate-800 px-6 flex items-center justify-between bg-slate-900/60 backdrop-blur-md">
          <div>
            <h2 className="text-xl font-bold text-white tracking-tight flex items-center gap-2">
              {currentBoard?.title || '載入中...'}
            </h2>
            <p className="text-xs text-slate-400">
              {currentBoard?.description || '專案團隊任務看板'}
            </p>
          </div>
          <div className="flex items-center gap-3">
            <button 
              onClick={() => setIsNewBoardOpen(true)}
              className="bg-slate-800 hover:bg-slate-700 text-slate-200 px-3.5 py-2 rounded-xl text-xs font-semibold flex items-center gap-1.5 transition border border-slate-700"
            >
              <Plus size={15} /> 新增看板
            </button>
          </div>
        </header>

        {/* 看板內容區 (橫向滾動) */}
        <div className="flex-1 overflow-x-auto p-6">
          {loading ? (
            <div className="h-full flex items-center justify-center text-slate-500 text-sm">
              載入看板資料中...
            </div>
          ) : (
            <div className="flex gap-5 h-full items-start">
              {currentBoard?.taskLists?.map(list => (
                <div 
                  key={list.id}
                  onDragOver={handleDragOver}
                  onDrop={(e) => handleDrop(e, list.id)}
                  className="w-80 bg-slate-950/70 border border-slate-800/80 rounded-2xl flex flex-col max-h-full shadow-xl shadow-black/20"
                >
                  {/* 列表標題欄 */}
                  <div className="p-4 border-b border-slate-800/80 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-sm text-slate-200">{list.title}</span>
                      <span className="bg-slate-800 text-slate-400 text-xs px-2 py-0.5 rounded-full font-medium">
                        {list.tasks?.length || 0}
                      </span>
                    </div>
                    <button 
                      onClick={() => {
                        setSelectedListId(list.id);
                        setIsNewTaskOpen(true);
                      }}
                      className="text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800 transition"
                      title="新增任務卡片"
                    >
                      <Plus size={16} />
                    </button>
                  </div>

                  {/* 任務卡片清單 (垂直滾動) */}
                  <div className="p-3 overflow-y-auto space-y-3 flex-1 min-h-[120px]">
                    {list.tasks?.map(task => (
                      <div
                        key={task.id}
                        draggable
                        onDragStart={(e) => handleDragStart(e, task.id)}
                        className="bg-slate-900 border border-slate-800 hover:border-slate-700 p-3.5 rounded-xl shadow-md cursor-grab active:cursor-grabbing hover:shadow-indigo-500/5 transition group relative"
                      >
                        <div className="flex items-start justify-between gap-2 mb-2">
                          <h4 className="font-medium text-sm text-slate-100 leading-snug">
                            {task.title}
                          </h4>
                          <button 
                            onClick={() => handleDeleteTask(task.id)}
                            className="opacity-0 group-hover:opacity-100 text-slate-500 hover:text-red-400 p-1 transition"
                            title="刪除卡片"
                          >
                            <Trash2 size={13} />
                          </button>
                        </div>

                        {task.description && (
                          <p className="text-xs text-slate-400 line-clamp-2 mb-3">
                            {task.description}
                          </p>
                        )}

                        <div className="flex items-center justify-between pt-1 border-t border-slate-800/60">
                          {getPriorityBadge(task.priority)}

                          {task.dueDate && (
                            <span className="text-[11px] text-slate-400 flex items-center gap-1 font-mono">
                              <Calendar size={11} className="text-slate-500" />
                              {task.dueDate}
                            </span>
                          )}
                        </div>
                      </div>
                    ))}

                    {(!list.tasks || list.tasks.length === 0) && (
                      <div className="h-24 border-2 border-dashed border-slate-800/60 rounded-xl flex items-center justify-center text-xs text-slate-600">
                        拖拉卡片至此處
                      </div>
                    )}
                  </div>

                  {/* 列表底部快速新增按鈕 */}
                  <div className="p-2 border-t border-slate-800/60">
                    <button
                      onClick={() => {
                        setSelectedListId(list.id);
                        setIsNewTaskOpen(true);
                      }}
                      className="w-full py-2 text-xs font-medium text-slate-400 hover:text-slate-200 hover:bg-slate-900 rounded-xl flex items-center justify-center gap-1 transition"
                    >
                      <Plus size={14} /> 新增卡片
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>

      {/* 新增任務 Modal */}
      {isNewTaskOpen && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md p-6 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-4">新增任務卡片</h3>
            <form onSubmit={handleCreateTask} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">任務標題 *</label>
                <input
                  type="text"
                  required
                  placeholder="例如：實作 JWT 認證機制"
                  value={taskTitle}
                  onChange={e => setTaskTitle(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500 transition"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">詳細說明</label>
                <textarea
                  rows={3}
                  placeholder="補充細節或驗收標準..."
                  value={taskDesc}
                  onChange={e => setTaskDesc(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500 transition resize-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">優先級</label>
                  <select
                    value={taskPriority}
                    onChange={e => setTaskPriority(e.target.value as Priority)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500 transition"
                  >
                    <option value="LOW">低 (LOW)</option>
                    <option value="MEDIUM">中 (MEDIUM)</option>
                    <option value="HIGH">高 (HIGH)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">截止日期</label>
                  <input
                    type="date"
                    value={taskDueDate}
                    onChange={e => setTaskDueDate(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500 transition"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsNewTaskOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-medium text-slate-400 hover:bg-slate-800 transition"
                >
                  取消
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-medium bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/30 transition"
                >
                  建立任務
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 新增看板 Modal */}
      {isNewBoardOpen && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md p-6 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-4">建立新看板</h3>
            <form onSubmit={handleCreateBoard} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">看板名稱 *</label>
                <input
                  type="text"
                  required
                  placeholder="例如：2026 Q4 產品研發"
                  value={boardTitle}
                  onChange={e => setBoardTitle(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500 transition"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">專案描述</label>
                <textarea
                  rows={3}
                  placeholder="簡要描述專案目標..."
                  value={boardDesc}
                  onChange={e => setBoardDesc(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500 transition resize-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsNewBoardOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs font-medium text-slate-400 hover:bg-slate-800 transition"
                >
                  取消
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl text-xs font-medium bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/30 transition"
                >
                  建立看板
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
export default App;
