# Task Management System

This repository contains the initial project scaffold for a full-stack task management system built with Java, Spring Boot, MySQL, React, Vite, and Docker.

## Phase 1 status

This milestone establishes the project structure and verifies that the backend and frontend can be built successfully before implementing the application features.

## Tech stack

- Java 21
- Spring Boot 3.3.x
- Maven
- MySQL 8
- React 18
- Vite
- Docker / Docker Compose

## Repository structure

```text
 task-management-system/
 ├─ backend/
 │  ├─ pom.xml
 │  ├─ Dockerfile
 │  └─ src/
 │     ├─ main/java/com/example/taskmanager/
 │     └─ main/resources/
 ├─ frontend/
 │  ├─ package.json
 │  ├─ Dockerfile
 │  ├─ vite.config.js
 │  ├─ index.html
 │  └─ src/
 ├─ .env.example
 ├─ .gitignore
 ├─ docker-compose.yml
 └─ README.md
```

## Build commands

### Backend

```bash
cd task-management-system/backend
mvn test
```

### Frontend

```bash
cd task-management-system/frontend
npm install
npm run build
```

## Next phases

- Phase 2: entities, repositories, and Flyway migrations
- Phase 3: CRUD APIs and validation
- Phase 4: authentication and security
- Phase 5: testing and fixes
- Phase 6: React integration
- Phase 7: Docker and CI/CD
- Phase 8: documentation and final review
