# Internal Controls Testing Application

A full-stack web application designed for managing and tracking internal controls testing.

## Overview
This application provides a comprehensive suite for managing internal controls:
- **Backend**: Spring Boot 3, Spring Security (JWT), Spring Data JPA, Redis Caching.
- **Frontend**: React (Vite) with a robust UI, Skeletons, Error Boundaries, and polished branding.
- **Database**: MySQL for persistence.
- **Cache**: Redis for rapid data retrieval.
- **Notifications**: Mailhog for catching and inspecting local email reminders.

## Architecture Diagram

```mermaid
graph TD
    UI[Frontend (React/Vite)] --> |REST API + JWT| App[Backend (Spring Boot)]
    App --> |JPA/Hibernate| DB[(MySQL Database)]
    App --> |Spring Cache| Redis[(Redis Cache)]
    App --> |SMTP| Mail[MailHog]
    
    subgraph Docker Compose
        UI
        App
        DB
        Redis
        Mail
    end
```

## Prerequisites
- Docker & Docker Compose
- Maven (optional, if running locally without Docker)
- Node.js 18+ (optional, if running locally without Docker)

## Setup Steps

1. **Clone the repository:**
   Ensure you are in the root directory `internal-controls-testing (2)`.

2. **Start the application using Docker Compose:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - **Frontend UI**: http://localhost:3000
   - **Backend API**: http://localhost:8080
   - **MailHog UI**: http://localhost:8025 (to view scheduled reminder emails)

4. **Testing Data:**
   The backend automatically seeds 30 records and two default users upon startup.
   - Admin User: `admin` / `admin123`
   - Normal User: `user` / `user123`

## Environment Variables (.env Reference)

The following variables are configured in `docker-compose.yml`. You can override them locally if running manually.

| Variable | Description | Default (Docker Compose) |
|---|---|---|
| `DB_URL` | MySQL Connection String | `jdbc:mysql://mysql:3306/ict_db` |
| `DB_USERNAME` | Database User | `root` |
| `DB_PASSWORD` | Database Password | `rootpassword` |
| `REDIS_HOST` | Redis Server Host | `redis` |
| `REDIS_PORT` | Redis Server Port | `6379` |
| `MAIL_HOST` | SMTP Host | `mailhog` |
| `MAIL_PORT` | SMTP Port | `1025` |
| `JWT_SECRET` | Secret key for JWT | `thisisaverylongandsecuresecretkeyfortesting12345` |
| `JWT_EXPIRATION` | Token expiry in ms | `86400000` (1 day) |
