# TaskFlow - 團隊任務看板與協作系統 (Team Kanban & Collaboration System)

> 🚀 專為求職打造的 Full-Stack 專案：Java 17 + Spring Boot 3 + MySQL 8 + React + TypeScript

---

## 📌 專案簡介
TaskFlow 是一個現代化團隊看板協作系統（類似 Trello / Jira），旨在展示企業級後端分層架構、複雜資料關聯處理、無狀態身份驗證、AOP 操作稽核以及前端拖拉排序等核心工程能力。

---

## 🛠️ 技術棧 (Tech Stack)

### 後端 (Backend)
- **語言**: Java 17 (LTS)
- **核心框架**: Spring Boot 3.4
- **ORM / 資料存取**: Spring Data JPA (Hibernate)
- **參數驗證**: Hibernate Validator (`@Valid`, `@NotBlank`, etc.)
- **文件工具**: SpringDoc OpenAPI (Swagger UI)
- **工具庫**: Project Lombok

### 資料庫 (Database)
- **資料庫**: MySQL 8.0 (Docker 容器化管理)
- **連線埠**: `3306`
- **資料庫名稱**: `taskflow_db`

### 前端 (Frontend)
- **語言**: TypeScript
- **框架**: React 18 / 19 + Vite
- **UI / 樣式**: Tailwind CSS + Lucide React
- **HTTP Client**: Axios

---

## 📂 專案結構
```text
taskflow/
├── backend/                # Spring Boot 後端專案
│   ├── src/main/java/com/taskflow/
│   │   ├── model/          # JPA Entity (Board, TaskList, Task, Priority)
│   │   ├── repository/     # Spring Data JPA 介面
│   │   ├── dto/            # 請求與回應資料傳輸物件 (DTO)
│   │   ├── service/        # 商業邏輯層
│   │   └── controller/     # RESTful API 控制層
│   └── pom.xml
├── docker-compose.yml      # MySQL 8 容器設定
└── README.md
```

---

## 🚀 快速開始

### 1. 啟動 MySQL 資料庫
```bash
docker compose up -d
```

### 2. 啟動後端服務
```bash
cd backend
./mvnw.cmd spring-boot:run
```
後端啟動後可訪問 Swagger 介面：[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
